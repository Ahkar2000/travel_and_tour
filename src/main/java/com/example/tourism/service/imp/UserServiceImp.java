package com.example.tourism.service.imp;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.entity.User;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.UserRequest;
import com.example.tourism.payLoad.response.UserResponse;
import com.example.tourism.repository.UserRepository;
import com.example.tourism.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImp extends BaseBusiness implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public BaseResponse register(UserRequest userRequest) {
        if(checkEmailDuplicate(userRequest.getEmail())){
            return new BaseResponse("409","Email already exists.");
        }
        User user = (User) changeUserRequest(userRequest);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new BaseResponse("000",convertUserResponse(user));
    }

    @Override
    public BaseResponse getUserById(Long id) {
        if(userExists(id) == null) return new BaseResponse("404", "User Not found.");
        User user = userExists(id);
        return new BaseResponse("000",convertUserResponse(user));
    }

    @Override
    public BaseResponse getUsers(Integer pageNo, Integer pageSize,String sortDir ,String sortField) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending(): Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,sort);
        Page<UserResponse> userResponses = userRepository.findAll(pageable).map(this::convertUserResponse);
        return new BaseResponse("00000",userResponses);

    }

    private UserResponse convertUserResponse(User user){
        return new UserResponse(user.getName(), user.getEmail(), user.getAddress(),user.getPhone(),user.getCreatedAt());
    }

}
