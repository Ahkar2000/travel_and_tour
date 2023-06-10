package com.example.tourism.service.imp;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.entity.Category;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.CategoryRequest;
import com.example.tourism.payLoad.response.CategoryResponse;
import com.example.tourism.repository.CategoryRepository;
import com.example.tourism.service.CategoryService;
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
public class CategoryServiceImp extends BaseBusiness implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Override
    public BaseResponse getCategories(Integer pageNo, Integer pageSize, String sortDir, String sortField) {
        try{
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending(): Sort.by(sortField).descending();
            Pageable pageable = PageRequest.of(pageNo-1,pageSize,sort);
            Page<CategoryResponse> categoryResponses = categoryRepository.findAll(pageable).map(this::convertCategoryResponse);
            return new BaseResponse("00000",categoryResponses);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse createCategory(CategoryRequest categoryRequest) {
        try{
            if(checkCategoryDuplicate(categoryRequest.getCategory())){
                return new BaseResponse("409","Category already exists.");
            }
            Category category = (Category) changeCategoryRequest(categoryRequest);
            category.setCreatedAt(LocalDateTime.now());
            categoryRepository.save(category);
            return new BaseResponse("000",convertCategoryResponse(category));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getCategoryById(Long id) {
        try{
            Optional<Category> categoryOptional = categoryRepository.findById(id);
            if(categoryOptional.isEmpty()){
                return new BaseResponse("404", "Category Not found.");
            }
            Category category = categoryOptional.get();
            return new BaseResponse("000",convertCategoryResponse(category));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getCategoryByName(String categoryName) {
        try{
            Optional<Category> categoryOptional = categoryRepository.findByCategory(categoryName);
            if(categoryOptional.isEmpty()){
                return new BaseResponse("404", "Category Not found.");
            }
            Category category = categoryOptional.get();
            return new BaseResponse("000",convertCategoryResponse(category));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        try{
            Category categoryToUpdate = categoryRepository.findById(id).orElse(null);
            if (categoryToUpdate == null ) return new BaseResponse("404","Category not found.");
            if (!categoryToUpdate.getCategory().equals(categoryRequest.getCategory())) {
                if (checkCategoryDuplicate(categoryRequest.getCategory())) {
                    return new BaseResponse("409", "Package already exists.");
                }
            }
            categoryToUpdate.setCategory(categoryRequest.getCategory());
            return new BaseResponse("000",convertCategoryResponse(categoryToUpdate));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse deleteById(Long id) {
        try{
            Optional<Category> categoryOptional = categoryRepository.findById(id);
            if(categoryOptional.isEmpty()){
                return new BaseResponse("404", "Category Not found.");
            }
            categoryRepository.deleteById(id);
            return new BaseResponse("000","Category is deleted.");
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
    private CategoryResponse convertCategoryResponse(Category category){
        return new CategoryResponse(category.getCategory(),category.getCreatedAt());
    }
}
