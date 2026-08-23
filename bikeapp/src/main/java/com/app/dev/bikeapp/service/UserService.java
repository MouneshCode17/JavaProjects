package com.app.dev.bikeapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dev.bikeapp.dto.RegisterRequest;
import com.app.dev.bikeapp.dto.UserResponse;
import com.app.dev.bikeapp.entity.Role;
import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.exception.UserAlreadyExistsException;
import com.app.dev.bikeapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request,Role role){

        String email = request.getEmail();
        String name = request.getName();
        String password = request.getPassword();

        if(!userRepository.existsByEmail(email)){
            User user = new User(name,email,passwordEncoder.encode(password),role);
            User savedUser = userRepository.save(user);

            return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
            );
        }else {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
    }

    public UserResponse toUserResponse(User user){
        return new UserResponse(user.getId(),user.getName(),user.getEmail(),user.getRole(),user.getCreatedAt());
    }

}
