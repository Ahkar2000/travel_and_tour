package com.example.tourism.service;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.CategoryRequest;

public interface CategoryService {
    BaseResponse getCategories(Integer pageNo, Integer pageSize, String sortDir, String sortField);
    BaseResponse createCategory(CategoryRequest categoryRequest);
    BaseResponse getCategoryById(Long id);
    BaseResponse getCategoryByName(String categoryName);
    BaseResponse updateCategory(Long id,CategoryRequest categoryRequest);
    BaseResponse deleteById(Long id);
}
