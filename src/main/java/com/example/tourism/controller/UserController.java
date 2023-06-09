package com.example.tourism.controller;

import com.example.tourism.payLoad.request.UserRequest;
import com.example.tourism.service.UserService;
import com.example.tourism.service.imp.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserServiceImp userService) {
        this.userService = userService;
    }
    @GetMapping("")
    @RolesAllowed("admin")
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortDir",defaultValue = "ASC") String sortDir,
            @RequestParam(name = "sortField",defaultValue = "id") String sortField){
        return ResponseEntity.ok(userService.getUsers(pageNo,pageSize,sortDir,sortField));
    }

    @GetMapping("/{id}")
    @RolesAllowed("admin")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.register(userRequest));
    }
}
