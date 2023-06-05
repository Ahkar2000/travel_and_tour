package com.example.tourism.service.imp;

import com.example.tourism.BaseResponse;
import com.example.tourism.entity.Category;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.CategoryRequest;
import com.example.tourism.payLoad.response.CategoryResponse;
import com.example.tourism.repository.CategoryRepository;
import com.example.tourism.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CategoryServiceImp extends BaseBusiness implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Override
    public BaseResponse getCategories(Integer pageNo, Integer pageSize, String sortDir, String sortField) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending(): Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,sort);
        Page<CategoryResponse> categoryResponses = categoryRepository.findAll(pageable).map(this::convertCategoryResponse);
        return new BaseResponse("00000",categoryResponses);
    }

    @Override
    public BaseResponse createCategory(CategoryRequest categoryRequest) {
        if(checkCategoryDuplicate(categoryRequest.getCategory())){
            return new BaseResponse("409","Category already exists.");
        }
        Category category = (Category) changeCategoryRequest(categoryRequest);
        category.setCreatedAt(LocalDateTime.now());
        categoryRepository.save(category);
        return new BaseResponse("000",convertCategoryResponse(category));
    }

    @Override
    public BaseResponse getCategoryById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isEmpty()){
            return new BaseResponse("404", "Category Not found.");
        }
        Category category = categoryOptional.get();
        return new BaseResponse("000",convertCategoryResponse(category));
    }

    @Override
    public BaseResponse getCategoryByName(String categoryName) {
        Optional<Category> categoryOptional = categoryRepository.findByCategory(categoryName);
        if(categoryOptional.isEmpty()){
            return new BaseResponse("404", "Category Not found.");
        }
        Category category = categoryOptional.get();
        return new BaseResponse("000",convertCategoryResponse(category));
    }

    @Override
    public BaseResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category categoryToUpdate = categoryRepository.findById(id).orElse(null);
        if (categoryToUpdate == null ) return new BaseResponse("404","Category not found.");
        if (!categoryToUpdate.getCategory().equals(categoryRequest.getCategory())) {
            if (checkCategoryDuplicate(categoryRequest.getCategory())) {
                return new BaseResponse("409", "Package already exists.");
            }
        }
        categoryToUpdate.setCategory(categoryRequest.getCategory());
        return new BaseResponse("000",convertCategoryResponse(categoryToUpdate));
    }

    @Override
    public BaseResponse deleteById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isEmpty()){
            return new BaseResponse("404", "Category Not found.");
        }
        categoryRepository.deleteById(id);
        return new BaseResponse("000","Category is deleted.");
    }
    private CategoryResponse convertCategoryResponse(Category category){
        return new CategoryResponse(category.getCategory(),category.getCreatedAt());
    }
}
