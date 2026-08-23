package com.app.dev.bikeapp.service;

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

    public RideResponse createRide(RideRequest rideRequest,Authentication authentication) {
        
        User rider = (User) authentication.getPrincipal();
        
        Ride ride = new Ride();
        
        ride.setRider(rider);
        ride.setPickupLocation(rideRequest.getPickupLocation());
        ride.setDropLocation(rideRequest.getDropoffLocation());
        ride.setStatus(RideStatus.REQUESTED);

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