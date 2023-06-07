package com.example.tourism.controller;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.CategoryRequest;
import com.example.tourism.service.imp.CategoryServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    CategoryServiceImp categoryServiceImp;
    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest){
        return ResponseEntity.ok(categoryServiceImp.createCategory(categoryRequest));
    }
    @GetMapping("")
    public ResponseEntity<BaseResponse> getAllCategories(
            @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize",defaultValue = "5") Integer pageSize,
            @RequestParam(name = "sortDir",defaultValue = "ASC") String sortDir,
            @RequestParam(name = "sortField",defaultValue = "id") String sortField
    ){
        return ResponseEntity.ok(categoryServiceImp.getCategories(pageNo, pageSize, sortDir, sortField));
    }
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<BaseResponse> getCategoryById(@PathVariable("id") Long id){
        return ResponseEntity.ok(categoryServiceImp.getCategoryById(id));
    }
    @GetMapping("/get-by-name/{categoryName}")
    public ResponseEntity<BaseResponse> getCategoryByName(@PathVariable("categoryName") String categoryName){
        return ResponseEntity.ok(categoryServiceImp.getCategoryByName(categoryName));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteById(@PathVariable("id") Long id){
        return ResponseEntity.ok(categoryServiceImp.deleteById(id));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateCategory(@Valid @RequestBody CategoryRequest categoryRequest, @PathVariable("id") Long id){
        return ResponseEntity.ok(categoryServiceImp.updateCategory(id,categoryRequest));
    }
}
