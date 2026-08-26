package com.app.dev.bikeapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.service.DriverService;

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
}
