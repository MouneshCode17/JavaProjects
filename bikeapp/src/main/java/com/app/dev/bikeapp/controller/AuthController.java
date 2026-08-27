package com.app.dev.bikeapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dev.bikeapp.dto.LoginRequest;
import com.app.dev.bikeapp.dto.LoginResponse;
import com.app.dev.bikeapp.dto.RegisterRequest;
import com.app.dev.bikeapp.dto.UserResponse;
import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.service.AuthService;
import com.app.dev.bikeapp.service.UserService;
import com.app.dev.bikeapp.entity.Role;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register/rider")
    public UserResponse registerRider(@RequestBody @Valid RegisterRequest request){

        return userService.register(request, Role.RIDER);
        
    }

    @PostMapping("/login")
    public LoginResponse login(
        @Valid @RequestBody LoginRequest request) {

    return authService.authenticateUser(request);
}

    @GetMapping("/current/user")
    public UserResponse getCurrentUser(Authentication authentication) {

        var user = (User)authentication.getPrincipal();

        return userService.toUserResponse(user);
    }

    @GetMapping("/rider")
    public String riderOnly() {
    return "Welcome Rider";
    }

    @PostMapping("/register/driver")
    public UserResponse registerDriver(@RequestBody @Valid RegisterRequest request) {
    return userService.register(request, Role.DRIVER);
    }

}