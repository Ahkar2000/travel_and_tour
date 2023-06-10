package com.example.tourism.service.imp;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.entity.User;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.UserRequest;
import com.example.tourism.payLoad.response.UserResponse;
import com.example.tourism.payLoad.response.WalletResponse;
import com.example.tourism.repository.UserRepository;
import com.example.tourism.service.KeyCloakService;
import com.example.tourism.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class UserServiceImp extends BaseBusiness implements UserService {
    private final UserRepository userRepository;

    private final KeyCloakService keyCloakService;

    private final RestTemplate restTemplate;

    public UserServiceImp(UserRepository userRepository, KeyCloakService keyCloakService, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.keyCloakService = keyCloakService;
        this.restTemplate = restTemplate;
    }

    @Override
    public BaseResponse register(UserRequest userRequest) {
        try{
            if(checkEmailDuplicate(userRequest.getEmail())){
                return new BaseResponse("409","Email already exists.");
            }
            UserRepresentation keyCloakUser = keyCloakService.createUser(userRequest);
            User user = (User) changeUserRequest(userRequest);
            if(keyCloakUser != null){
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);
            }else{
                return new BaseResponse("402","Can't create user.");
            }
            return new BaseResponse("000",convertUserResponse(user));
        }catch (Exception e){
            return new BaseResponse("402",e.getMessage());
        }
    }

    @Override
    public BaseResponse getUserById(Long id) {
        try{
            if(userExists(id) == null) return new BaseResponse("404", "User Not found.");
            User user = userExists(id);
            return new BaseResponse("000",convertUserResponse(user));
        }catch (Exception e){
            return new BaseResponse("402",e.getMessage());
        }
    }

    @Override
    public BaseResponse getUsers(Integer pageNo, Integer pageSize,String sortDir ,String sortField) {
        try{
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending(): Sort.by(sortField).descending();
            Pageable pageable = PageRequest.of(pageNo-1,pageSize,sort);
            Page<UserResponse> userResponses = userRepository.findAll(pageable).map(this::convertUserResponse);
            return new BaseResponse("00000",userResponses);
        }catch (Exception e){
            return new BaseResponse("402",e.getMessage());
        }

    }

    private UserResponse convertUserResponse(User user){
        return new UserResponse(user.getName(), user.getEmail(), user.getAddress(),user.getPhone(),user.getCreatedAt());
    }
}
