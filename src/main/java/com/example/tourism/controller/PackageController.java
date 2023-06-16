package com.example.tourism.controller;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.PackageRequest;
import com.example.tourism.repository.PackageRepository;
import com.example.tourism.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping("/packages")
public class PackageController {
    @Autowired
    PackageService packageService;
    @Autowired
    PackageRepository packageRepository;

    @GetMapping("")
    public ResponseEntity<BaseResponse> getPackages(
            @RequestParam(name = "categoryId",required = false) Long categoryId,
            @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortDir",defaultValue = "ASC") String sortDir,
            @RequestParam(name = "sortField",defaultValue = "id") String sortField){
        return ResponseEntity.ok(packageService.getPackages(categoryId, pageNo,pageSize,sortDir,sortField));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<BaseResponse> getPackageById(@PathVariable("id") Long id){
        return ResponseEntity.ok(packageService.getPackageById(id));
    }
    @GetMapping("/get-by-name/{packageName}")
    public ResponseEntity<BaseResponse> getPackageByPackageName(@PathVariable("packageName") String packageName){
        return ResponseEntity.ok(packageService.getPackageByPackageName(packageName));
    }
    @GetMapping("/popular-packages")
    public ResponseEntity<BaseResponse> getPopularPackages(){
        return ResponseEntity.ok(packageService.getPopularPackages());
    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> deleteById(@PathVariable("id") Long id){
        return ResponseEntity.ok(packageService.deleteById(id));
    }

    @PostMapping("/create")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> createPackage(@Valid @RequestBody PackageRequest packageRequest){
        return ResponseEntity.ok(packageService.createPackage(packageRequest));
    }

    @PutMapping("/update/{id}")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> updatePackage(@Valid @RequestBody PackageRequest packageRequest,@PathVariable("id") Long id){
        return ResponseEntity.ok(packageService.updatePackage(id,packageRequest));
    }
}
