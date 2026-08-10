package com.app.dev.bikeapp.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dev.bikeapp.dto.RegisterRequest;
import com.app.dev.bikeapp.dto.UserResponse;
import com.app.dev.bikeapp.entity.Role;
import com.app.dev.bikeapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register/rider")
    public UserResponse registerDriver(@RequestBody @Valid RegisterRequest request){

        return userService.register(request, Role.RIDER);
        
    }
    
}
