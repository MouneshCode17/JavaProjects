package com.app.dev.bikeapp.service;

import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.dev.bikeapp.repository.DriverProfileRepository;

import lombok.RequiredArgsConstructor;
import com.app.dev.bikeapp.entity.DriverProfile;
import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.entity.Ride;
import com.app.dev.bikeapp.dto.RideStatus;
import com.app.dev.bikeapp.dto.RideResponse;
import com.app.dev.bikeapp.repository.RideRepository;


@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;
    private final RideRepository rideRepository;

    public void updateAvailability(
        UUID userId,
        boolean available) {

    var driverProfile = driverProfileRepository
            .findByUserId(userId)
            .orElseThrow(() ->
                    new RuntimeException("Driver profile not found"));

    driverProfile.setAvailable(available);

    driverProfileRepository.save(driverProfile);
}

    public List<DriverProfile> getAvailableDrivers() {
        
        return driverProfileRepository.findByAvailableTrue();
    }

    public RideResponse acceptRide(UUID rideId, User driver) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new RuntimeException("Ride not found"));

    if (ride.getDriver() == null
            || !ride.getDriver().getId().equals(driver.getId())) {
        throw new RuntimeException("You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.REQUESTED) {
        throw new RuntimeException("Ride cannot be accepted");
    }

    ride.setStatus(RideStatus.ACCEPTED);

    Ride savedRide = rideRepository.save(ride);

    return new RideResponse(
            savedRide.getId(),
            savedRide.getRider().getId(),
            savedRide.getDriver().getId(),
            savedRide.getPickupLocation(),
            savedRide.getDropLocation(),
            savedRide.getStatus(),
            savedRide.getFare(),
            savedRide.getCreatedAt(),
            savedRide.getUpdatedAt()
    );
}

    public RideResponse startRide(UUID rideId, User driver) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new RuntimeException("Ride not found"));

    if (ride.getDriver() == null ||
            !ride.getDriver().getId().equals(driver.getId())) {

        throw new RuntimeException(
                "You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.ACCEPTED) {
        throw new RuntimeException(
                "Ride cannot be started");
    }

    ride.setStatus(RideStatus.STARTED);

    Ride savedRide = rideRepository.save(ride);

    return new RideResponse(
            savedRide.getId(),
            savedRide.getRider().getId(),
            savedRide.getDriver().getId(),
            savedRide.getPickupLocation(),
            savedRide.getDropLocation(),
            savedRide.getStatus(),
            savedRide.getFare(),
            savedRide.getCreatedAt(),
            savedRide.getUpdatedAt()
    );
}

    public RideResponse completeRide(UUID rideId, User driver) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new RuntimeException("Ride not found"));

    if (ride.getDriver() == null ||
            !ride.getDriver().getId().equals(driver.getId())) {

        throw new RuntimeException(
                "You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.STARTED) {
        throw new RuntimeException(
                "Ride cannot be completed");
    }

    ride.setStatus(RideStatus.COMPLETED);

    Ride savedRide = rideRepository.save(ride);

    return new RideResponse(
            savedRide.getId(),
            savedRide.getRider().getId(),
            savedRide.getDriver().getId(),
            savedRide.getPickupLocation(),
            savedRide.getDropLocation(),
            savedRide.getStatus(),
            savedRide.getFare(),
            savedRide.getCreatedAt(),
            savedRide.getUpdatedAt()
    );
}
}
