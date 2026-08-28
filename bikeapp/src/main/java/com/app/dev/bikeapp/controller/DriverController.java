package com.app.dev.bikeapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.service.DriverService;

import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import com.app.dev.bikeapp.dto.RideResponse;
import com.app.dev.bikeapp.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/driver")
public class DriverController {

    private final DriverService driverService;

    @PatchMapping("/availability")
    public String getDriverAvailability(@RequestParam boolean available,
                                        Authentication authentication
    ){

        User user = (User) authentication.getPrincipal();

        driverService.updateAvailability(user.getId(),available);

        return available ? "Driver is Online" : "Driver is Offline";

    }

    @PatchMapping("/rides/{rideId}/accept")
    public RideResponse acceptRide(
        @PathVariable UUID rideId,
        Authentication authentication) {

            System.out.println("ACCEPT RIDE CONTROLLER REACHED");

    User driver = (User) authentication.getPrincipal();

    return driverService.acceptRide(rideId, driver);
}

    @PatchMapping("/rides/{rideId}/start")
    public RideResponse startRide(
        @PathVariable UUID rideId,
        Authentication authentication) {

    User driver = (User) authentication.getPrincipal();

    return driverService.startRide(rideId, driver);
}

    @PatchMapping("/rides/{rideId}/complete")
    public RideResponse completeRide(
        @PathVariable UUID rideId,
        Authentication authentication) {

    User driver = (User) authentication.getPrincipal();

    return driverService.completeRide(rideId, driver);
}
}
