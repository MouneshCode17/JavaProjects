package com.app.dev.bikeapp.service;

import com.app.dev.bikeapp.repository.DriverProfileRepository;
import com.app.dev.bikeapp.repository.RideRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.app.dev.bikeapp.dto.RideRequest;
import com.app.dev.bikeapp.dto.RideResponse;
import com.app.dev.bikeapp.dto.RideStatus;
import com.app.dev.bikeapp.entity.Ride;
import com.app.dev.bikeapp.entity.User;


@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverProfileRepository driverProfileRepository;

    public RideResponse createRide(RideRequest rideRequest,Authentication authentication) {
        
        User rider = (User) authentication.getPrincipal();
        
        Ride ride = new Ride();
        
        ride.setRider(rider);
        ride.setPickupLocation(rideRequest.getPickupLocation());
        ride.setDropLocation(rideRequest.getDropoffLocation());
        ride.setStatus(RideStatus.REQUESTED);

        var availableDrivers = driverProfileRepository.findByAvailableTrue();
        if(availableDrivers.isEmpty()){
            throw new RuntimeException("No available drivers at the moment");
        }

        var driverProfile = availableDrivers.get(0);
        ride.setDriver(driverProfile.getUser());

        System.out.println("ASSIGNED DRIVER: " + driverProfile.getUser().getId());
        driverProfile.setAvailable(false);
        driverProfileRepository.save(driverProfile);

        Ride savedRide = rideRepository.save(ride);

        return new RideResponse(
            savedRide.getId(),
            savedRide.getRider().getId(),
            savedRide.getDriver() != null
                    ? savedRide.getDriver().getId()
                    : null,
            savedRide.getPickupLocation(),
            savedRide.getDropLocation(),
            savedRide.getStatus(),
            savedRide.getFare(),
            savedRide.getCreatedAt(),
            savedRide.getUpdatedAt()
    );

    }

}