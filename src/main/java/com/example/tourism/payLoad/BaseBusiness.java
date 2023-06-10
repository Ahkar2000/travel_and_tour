package com.example.tourism.payLoad;

import com.example.tourism.entity.Booking;
import com.example.tourism.entity.Category;
import com.example.tourism.entity.Package;
import com.example.tourism.entity.User;
import com.example.tourism.payLoad.request.BookingRequest;
import com.example.tourism.payLoad.request.CategoryRequest;
import com.example.tourism.payLoad.request.PackageRequest;
import com.example.tourism.payLoad.request.UserRequest;
import com.example.tourism.repository.BookingRepository;
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
    @Autowired
    BookingRepository bookingRepository;
    public Object changeUserRequest(Object object){
        UserRequest userRequest = (UserRequest) object;
        return new User(userRequest.getName(),userRequest.getEmail(), userRequest.getAddress(), userRequest.getPhone());
    }
    public Object changePackageRequest(Object object){
        PackageRequest packageRequest = (PackageRequest) object;
        return new Package(
                packageRequest.getPackageName(),packageRequest.getDescription(),packageRequest.getGroupSize(),packageRequest.getDuration(),
                packageRequest.getPlaces(),packageRequest.getTransportation(),packageRequest.getPrice(),packageRequest.getCategoryId()
        );
    }
    public Object changeCategoryRequest(Object object){
        CategoryRequest categoryRequest = (CategoryRequest) object;
        return new Category(categoryRequest.getCategory());
    }
    public Object changeBookingRequest(Object object){
        BookingRequest bookingRequest = (BookingRequest) object;
        return new Booking(
                bookingRequest.getUserId(),bookingRequest.getPackageId(),
                bookingRequest.getGroupSize(),bookingRequest.getSchedule()
        );
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
    public Package packageExists(Long id){
        Optional<Package> packageCheck = packageRepository.findById(id);
        if(packageCheck.isPresent()) return packageCheck.get();
        return null;
    }
    public User userExists(Long id){
        Optional<User> userCheck = userRepository.findById(id);
        if(userCheck.isPresent()) return userCheck.get();
        return null;
    }
    public Booking bookingExists(Long id){
        Optional<Booking> bookingCheck = bookingRepository.findById(id);
        if(bookingCheck.isPresent()) return bookingCheck.get();
        return null;
    }
    public Category categoryExists(Long id){
        Optional<Category> categoryCheck = categoryRepository.findById(id);
        if(categoryCheck.isPresent()) return categoryCheck.get();
        return null;
    }
}
