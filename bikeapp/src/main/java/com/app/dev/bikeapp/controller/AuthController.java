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

import com.app.dev.bikeapp.dto.LoginRequest;
import com.app.dev.bikeapp.dto.LoginResponse;
import com.app.dev.bikeapp.service.AuthService;


@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register/rider")
    public UserResponse registerDriver(@RequestBody @Valid RegisterRequest request){

        return userService.register(request, Role.RIDER);
        
    }

    @PostMapping("/login")
    public LoginResponse login(
        @Valid @RequestBody LoginRequest request) {

    return authService.authenticateUser(request);
}


    
}
