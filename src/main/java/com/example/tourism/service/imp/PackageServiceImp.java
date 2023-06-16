package com.example.tourism.service.imp;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.entity.Category;
import com.example.tourism.entity.Package;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.PackageRequest;
import com.example.tourism.payLoad.response.PackageResponse;
import com.example.tourism.payLoad.response.PopularPackagesResponse;
import com.example.tourism.projection.PopularPackageProjection;
import com.example.tourism.repository.CategoryRepository;
import com.example.tourism.repository.PackageRepository;
import com.example.tourism.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PackageServiceImp extends BaseBusiness implements PackageService {


    @Autowired
    PackageRepository packageRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public BaseResponse updatePackage(Long id, PackageRequest packageRequest) {
        try {
            Package packageToUpdate = packageRepository.findById(id).orElse(null);
            if (packageToUpdate == null) return new BaseResponse("404", "Package not found.");
            if (!packageToUpdate.getPackageName().equalsIgnoreCase(packageRequest.getPackageName())) {
                if (checkPackageDuplicate(packageRequest.getPackageName())) {
                    return new BaseResponse("409", "Package already exists.");
                }
            }
            Category category = categoryRepository.findById(packageRequest.getCategoryId()).orElse(null);
            if(category == null)return new BaseResponse("404", "Category does not exist.");
            packageToUpdate.setPackageName(packageRequest.getPackageName());
            packageToUpdate.setDescription(packageRequest.getDescription());
            packageToUpdate.setPlaces(packageRequest.getPlaces());
            packageToUpdate.setDuration(packageRequest.getDuration());
            packageToUpdate.setGroupSize(packageRequest.getGroupSize());
            packageToUpdate.setPrice(packageRequest.getPrice());
            packageToUpdate.setTransportation(packageRequest.getTransportation());
            packageRepository.save(packageToUpdate);
            return new BaseResponse("000", convertPackageResponse(packageToUpdate));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getPackages(Long categoryId, Integer pageNo, Integer pageSize, String sortDir, String sortField) {
        try{
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
            Page<PackageResponse> packageResponses = packageRepository.getPackagesByCategory(categoryId, pageable).map(this::convertPackageResponse);
            return new BaseResponse("00000", packageResponses);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getPopularPackages() {
        try{
            return new BaseResponse("000",packageRepository.findPopularPackages().stream().map(this::convertPopular));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse createPackage(PackageRequest packageRequest) {
        try{
            if (checkPackageDuplicate(packageRequest.getPackageName())) {
                return new BaseResponse("409", "Package already exists.");
            }
            Optional<Category> categoryOptional = categoryRepository.findById(packageRequest.getCategoryId());
            if(categoryOptional.isEmpty())return new BaseResponse("404", "Category does not exist.");
            Package aPackage = (Package) changePackageRequest(packageRequest);
            aPackage.setCreatedAt(LocalDateTime.now());
            packageRepository.save(aPackage);
            return new BaseResponse("000", convertPackageResponse(aPackage));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getPackageById(Long id) {
        try {
            if (packageExists(id) == null) return new BaseResponse("404", "Package Not found.");
            Package apackage = packageExists(id);
            return new BaseResponse("000", convertPackageResponse(apackage));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getPackageByPackageName(String packageName) {
        try{
            Optional<Package> packageCheck = packageRepository.findByPackageName(packageName);
            if (packageCheck.isEmpty()) {
                return new BaseResponse("404", "Package Not found.");
            }
            Package apackage = packageCheck.get();
            return new BaseResponse("000", convertPackageResponse(apackage));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    private PackageResponse convertPackageResponse(Package apackage) {
        return new PackageResponse(apackage.getPackageName(), apackage.getDescription(), apackage.getGroupSize(), apackage.getDuration(), apackage.getPlaces(), apackage.getTransportation(), apackage.getPrice(), apackage.getCategoryId(), apackage.getCreatedAt());
    }
    private PopularPackagesResponse convertPopular(PopularPackageProjection popularPackageProjection){
        return new PopularPackagesResponse(popularPackageProjection.getPackageName(), popularPackageProjection.getBookingCount(                                                                                                                               ));
    }
    public BaseResponse deleteById(Long id) {
        try{
            if (packageExists(id) == null) return new BaseResponse("404", "Package Not found.");
            packageRepository.deleteById(id);
            return new BaseResponse("000", "Package is deleted.");
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
}
