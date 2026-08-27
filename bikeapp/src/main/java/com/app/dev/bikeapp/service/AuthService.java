package com.app.dev.bikeapp.service;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.dev.bikeapp.dto.LoginRequest;
import com.app.dev.bikeapp.dto.LoginResponse;
import com.app.dev.bikeapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService{

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse authenticateUser(LoginRequest loginRequest){
        
        var phoneNumber = loginRequest.getPhoneNumber();
        var password = loginRequest.getPassword();

        var userObject = userRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() ->
                new AuthenticationCredentialsNotFoundException(
                        "User phone number or password are incorrect"
                ));

        if(!passwordEncoder.matches(password, userObject.getPassword()))    throw new AuthenticationCredentialsNotFoundException("User Email and password are incorrect");

            var token = jwtService.generateToken(userObject);

            log.info("Token generated successfully");
            
            return new LoginResponse(token);
            
    }
}