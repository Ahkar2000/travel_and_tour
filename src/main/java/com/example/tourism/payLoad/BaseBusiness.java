package com.example.tourism.payLoad;

import com.example.tourism.entity.Category;
import com.example.tourism.entity.Package;
import com.example.tourism.entity.User;
import com.example.tourism.payLoad.request.CategoryRequest;
import com.example.tourism.payLoad.request.PackageRequest;
import com.example.tourism.payLoad.request.UserRequest;
import com.example.tourism.repository.CategoryRepository;
import com.example.tourism.repository.PackageRepository;
import com.example.tourism.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class BaseBusiness {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    CategoryRepository categoryRepository;
    public Object changeUserRequest(Object object){
        UserRequest userRequest = (UserRequest) object;
        return new User(userRequest.getName(),userRequest.getEmail(), userRequest.getPassword(), userRequest.getAddress(), userRequest.getPhone());
    }
    public Object changePackageRequest(Object object){
        PackageRequest packageRequest = (PackageRequest) object;
        return new Package(packageRequest.getPackageName(),packageRequest.getDescription(),packageRequest.getGroupSize(),packageRequest.getDuration(),packageRequest.getPlaces(),packageRequest.getTransportation(),packageRequest.getPrice(),packageRequest.getCategoryId());
    }
    public Object changeCategoryRequest(Object object){
        CategoryRequest categoryRequest = (CategoryRequest) object;
        return new Category(categoryRequest.getCategory());
    }

    public boolean checkEmailDuplicate(String email){
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }
    public boolean checkPackageDuplicate(String name){
        Optional<Package> packageCheck = packageRepository.findByPackageName(name);
        return packageCheck.isPresent();
    }
    public boolean checkCategoryDuplicate(String category){
        Optional<Category> categoryCheck = categoryRepository.findByCategory(category);
        return categoryCheck.isPresent();
    }
}
