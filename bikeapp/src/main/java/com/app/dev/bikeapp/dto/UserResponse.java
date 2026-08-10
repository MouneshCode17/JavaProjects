package com.app.dev.bikeapp.dto;

import java.time.Instant;
import java.util.UUID;

import com.app.dev.bikeapp.entity.Role;

public record UserResponse (

     UUID id,
     String name,
     String email,
     Role role,
     Instant createdAt
){
    
}
