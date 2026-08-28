package com.app.dev.bikeapp.service;

import java.util.UUID;

import com.app.dev.bikeapp.repository.RideRepository;
import lombok.RequiredArgsConstructor;

import com.app.dev.bikeapp.exception.ResourceNotFoundException;
import com.app.dev.bikeapp.exception.UnAuthorizedRideException;
import com.app.dev.bikeapp.exception.InvalidRideStateException;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.app.dev.bikeapp.dto.RideRequest;
import com.app.dev.bikeapp.dto.RideResponse;
import com.app.dev.bikeapp.dto.RideStatus;
import com.app.dev.bikeapp.entity.Ride;
import com.app.dev.bikeapp.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    
    @Transactional
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

    public RideResponse cancelRide(UUID rideId, User rider) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Ride not found"));


        var riderCheck = ride.getRider();
    if (riderCheck == null || riderCheck.getId() == null || !riderCheck.getId().equals(rider.getId())) {
        throw new UnAuthorizedRideException(
                "You are not the rider of this ride");
    }

    if (ride.getStatus() == RideStatus.COMPLETED ||
        ride.getStatus() == RideStatus.CANCELLED ||
        ride.getStatus() == RideStatus.STARTED) {

        throw new InvalidRideStateException(
                "Ride cannot be cancelled");
    }

    ride.setStatus(RideStatus.CANCELLED);

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