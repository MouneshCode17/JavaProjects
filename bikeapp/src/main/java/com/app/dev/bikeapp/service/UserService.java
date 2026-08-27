package com.app.dev.bikeapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dev.bikeapp.dto.RegisterRequest;
import com.app.dev.bikeapp.dto.UserResponse;
import com.app.dev.bikeapp.entity.DriverProfile;
import com.app.dev.bikeapp.entity.Role;
import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.exception.UserAlreadyExistsException;
import com.app.dev.bikeapp.repository.DriverProfileRepository;
import com.app.dev.bikeapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverProfileRepository driverProfileRepository;

    @Transactional
    public UserResponse register(RegisterRequest request,Role role){

        String email = request.getEmail();
        String name = request.getName();
        String password = request.getPassword();
        String phoneNumber = request.getPhoneNumber();

        if(userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)){
    
            throw new UserAlreadyExistsException("User with this email or phoneNumber already exists");
        }else 
            {

            User user = new User(name,email,phoneNumber,passwordEncoder.encode(password),role);
            User savedUser = userRepository.save(user);

            var userRole = savedUser.getRole();

            if(userRole == Role.DRIVER){

                DriverProfile driverProfile = new DriverProfile();

                driverProfile.setUser(savedUser);
                driverProfile.setAvailable(false);
                driverProfileRepository.save(driverProfile);

            }

            return toUserResponse(savedUser);
        }
    }

    public UserResponse toUserResponse(User user){
        return new UserResponse(user.getId(),user.getName(),user.getEmail(),user.getRole(),user.getCreatedAt());
    }

}
