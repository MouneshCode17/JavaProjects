package com.app.dev.bikeapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import com.app.dev.bikeapp.entity.DriverProfile;


import lombok.RequiredArgsConstructor;

import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.service.DriverService;
import com.app.dev.bikeapp.dto.StartRideRequest;
import com.app.dev.bikeapp.dto.DriverCancellationRequest;

import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import com.app.dev.bikeapp.dto.RideResponse;
import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.dto.DriverLocationRequest;
import jakarta.validation.Valid;

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
        Authentication authentication,
        @RequestBody StartRideRequest startRideRequest) {

    User driver = (User) authentication.getPrincipal();

    return driverService.startRide(rideId, driver, startRideRequest.getRidePin());
}

    @PatchMapping("/rides/{rideId}/complete")
    public RideResponse completeRide(

        @PathVariable UUID rideId,
        Authentication authentication) {

    User driver = (User) authentication.getPrincipal();

    return driverService.completeRide(rideId, driver);
}

    @PatchMapping("/rides/{rideId}/request-cancellation")
    public String requestCancellation(
        @PathVariable UUID rideId,
        Authentication authentication) {

    User driver = (User) authentication.getPrincipal();

    String otp = driverService.requestCancellation(rideId, driver);

    return "Cancellation OTP: " + otp;
}

    @PatchMapping("/rides/{rideId}/cancel")
    public RideResponse cancelRide(
        @PathVariable UUID rideId,
        @RequestBody DriverCancellationRequest request,
        Authentication authentication) {

    User driver = (User) authentication.getPrincipal();

    System.out.println("Driver cancellation request received for rideId: " + rideId + " with OTP: " + request.getCancellationOtp());

    return driverService.cancelRideByDriver(
            rideId,
            driver,
            request.getCancellationOtp()
    );
}

    @PatchMapping("/location")
    public String updateLocation(
        @Valid @RequestBody DriverLocationRequest request,
        Authentication authentication) {

    User driver = (User) authentication.getPrincipal();

    driverService.updateLocation(
            driver.getId(),
            request.getLatitude(),
            request.getLongitude()
    );

    return "Driver location updated successfully";
}

    @GetMapping("/nearby")
    public List<DriverProfile> getNearbyDrivers(
        @RequestParam double latitude,
        @RequestParam double longitude,
        @RequestParam(defaultValue = "5") double radiusKm) {

    return driverService.getNearbyDrivers(
            latitude,
            longitude,
            radiusKm
    );
}
}
