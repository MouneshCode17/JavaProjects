package com.app.dev.bikeapp.service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

import com.app.dev.bikeapp.repository.RideRepository;
import com.app.dev.bikeapp.repository.DriverProfileRepository;
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
import com.app.dev.bikeapp.service.FareService;
import com.app.dev.bikeapp.service.LocationService;
import com.app.dev.bikeapp.repository.DriverProfileRepository;
import com.app.dev.bikeapp.entity.DriverProfile;
import com.app.dev.bikeapp.dto.DriverInfoResponse;
import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final FareService fareService;
    private final LocationService locationService;
    private final DriverProfileRepository driverProfileRepository;

    
    @Transactional
    public RideResponse createRide(RideRequest rideRequest,Authentication authentication) {
        

        User rider = (User) authentication.getPrincipal();

        var activeRide = rideRepository.findFirstByRiderIdAndStatusIn(
        rider.getId(),
        List.of(
                RideStatus.REQUESTED,
                RideStatus.ACCEPTED,
                RideStatus.STARTED
        )
        );

        if (activeRide.isPresent()) {
            throw new InvalidRideStateException(
            "You already have an active ride");
}
        
        Ride ride = new Ride();
        ride.setRider(rider);
        ride.setPickupLocation(rideRequest.getPickupLocation());
        ride.setPickupLatitude(rideRequest.getPickupLatitude());
        ride.setPickupLongitude(rideRequest.getPickupLongitude());
        ride.setDropLocation(rideRequest.getDropoffLocation());
        ride.setDropLatitude(rideRequest.getDropLatitude());
        ride.setDropLongitude(rideRequest.getDropLongitude());

        double distanceKm = locationService.calculateDistance(
            rideRequest.getPickupLatitude(),
            rideRequest.getPickupLongitude(),
            rideRequest.getDropLatitude(),
            rideRequest.getDropLongitude()
        );

        BigDecimal fare = fareService.calculateFare(distanceKm);
        ride.setFare(fare);

        ride.setStatus(RideStatus.REQUESTED);

        

        Ride savedRide = rideRepository.save(ride);

        return toRideResponse(savedRide);

    }

    @Transactional
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

    if(ride.getStatus() == RideStatus.ACCEPTED){

        var driverProfile = driverProfileRepository
            .findByUserId(ride.getDriver().getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Driver profile not found"));

    driverProfile.setAvailable(true);
    driverProfileRepository.save(driverProfile);
    }

    BigDecimal cancellationFee = fareService.calculateCancellationFee(ride.getStatus(), ride.getFare());
    ride.setCancellationFee(cancellationFee);

    ride.setStatus(RideStatus.CANCELLED);

    Ride savedRide = rideRepository.save(ride);

    return toRideResponse(savedRide);
}


    public RideResponse getActiveRide(User rider) {

    Ride ride = rideRepository
            .findFirstByRiderIdAndStatusIn(
                    rider.getId(),
                    List.of(
                            RideStatus.REQUESTED,
                            RideStatus.ACCEPTED,
                            RideStatus.STARTED
                    )
            )
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "No active ride found"));

    return toRideResponse(ride);
}


    private RideResponse toRideResponse(Ride ride) {

    DriverInfoResponse driverInfo = null;

    if (ride.getDriver() != null) {
        driverInfo = new DriverInfoResponse(
                ride.getDriver().getId(),
                ride.getDriver().getName(),
                ride.getDriver().getPhoneNumber()
        );
    }

    return new RideResponse(
            ride.getId(),
            ride.getRider().getId(),
            ride.getDriver() != null
                    ? ride.getDriver().getId()
                    : null,
            ride.getPickupLocation(),
            ride.getDropLocation(),
            ride.getStatus(),
            ride.getFare(),
            ride.getCreatedAt(),
            ride.getUpdatedAt(),
            ride.getCancellationFee(),
            driverInfo
    );
}
}