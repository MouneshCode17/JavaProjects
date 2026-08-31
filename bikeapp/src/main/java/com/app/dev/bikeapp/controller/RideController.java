package com.app.dev.bikeapp.controller;

import java.util.UUID;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.app.dev.bikeapp.entity.User;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dev.bikeapp.dto.RideRequest;
import com.app.dev.bikeapp.dto.RideResponse;
import com.app.dev.bikeapp.service.RideService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rider")
public class RideController {

    private final RideService rideService;

    @PostMapping("/ride/creation")
    
    public RideResponse createRide(@Valid @RequestBody RideRequest request, Authentication authentication){

        return rideService.createRide(request, authentication);
    }

    @PatchMapping("/rides/{rideId}/cancel") 
     public RideResponse cancelRide(
        @PathVariable UUID rideId,
        Authentication authentication) {

    User rider = (User) authentication.getPrincipal();

    return rideService.cancelRide(rideId, rider);
}

    @GetMapping("/ride/active")
    public RideResponse getActiveRide(Authentication authentication) {

    User rider = (User) authentication.getPrincipal();

    return rideService.getActiveRide(rider);
}
}
