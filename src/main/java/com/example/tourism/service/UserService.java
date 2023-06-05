package com.example.tourism.service;

import com.example.tourism.BaseResponse;
import com.example.tourism.entity.User;
import com.example.tourism.payLoad.request.UserRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    BaseResponse getUsers(Integer pageNo, Integer pageSize,String sortDir, String sortField);
    BaseResponse register(UserRequest userRequest);
    BaseResponse getUserById(Long id);


}
