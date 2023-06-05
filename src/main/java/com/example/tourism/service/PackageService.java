package com.example.tourism.service;

import com.example.tourism.BaseResponse;
import com.example.tourism.payLoad.request.PackageRequest;
import com.example.tourism.payLoad.request.UserRequest;

public interface PackageService {
    BaseResponse getPackages(Long categoryId, Integer pageNo, Integer pageSize, String sortDir, String sortField);

    BaseResponse createPackage(PackageRequest packageRequest);

    BaseResponse getPackageById(Long id);

    BaseResponse getPackageByPackageName(String packageName);

    BaseResponse updatePackage(Long id, PackageRequest packageRequest);

    BaseResponse deleteById(Long id);
}
