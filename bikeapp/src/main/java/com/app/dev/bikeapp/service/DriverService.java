package com.app.dev.bikeapp.service;

import java.util.UUID;
import java.util.List;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.app.dev.bikeapp.repository.DriverProfileRepository;

import lombok.RequiredArgsConstructor;
import com.app.dev.bikeapp.entity.DriverProfile;
import com.app.dev.bikeapp.entity.User;
import com.app.dev.bikeapp.entity.Ride;
import com.app.dev.bikeapp.dto.RideStatus;
import com.app.dev.bikeapp.dto.RideResponse;
import com.app.dev.bikeapp.repository.RideRepository;
import com.app.dev.bikeapp.service.LocationService;

import com.app.dev.bikeapp.exception.ResourceNotFoundException;
import com.app.dev.bikeapp.exception.UnAuthorizedRideException;
import com.app.dev.bikeapp.exception.InvalidRideStateException;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;
    private final RideRepository rideRepository;
    private final LocationService locationService;

    public void updateAvailability(
        UUID userId,
        boolean available) {

    var driverProfile = driverProfileRepository
            .findByUserId(userId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Driver profile not found"));

    driverProfile.setAvailable(available);

    driverProfileRepository.save(driverProfile);
}

    public List<DriverProfile> getAvailableDrivers() {
        
        return driverProfileRepository.findFirstByAvailableTrue();
    }

    @Transactional
    public RideResponse acceptRide(UUID rideId, User driver) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Ride not found"));

    if (ride.getStatus() != RideStatus.REQUESTED) {
        throw new InvalidRideStateException("Ride cannot be accepted");
    }

    ride.setDriver(driver);

    ride.setStatus(RideStatus.ACCEPTED);

    setDriverAvailability(driver.getId(), false);
        
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

    public RideResponse startRide(UUID rideId, User driver, String ridePin) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Ride not found"));

    if (ride.getDriver() == null ||
            !ride.getDriver().getId().equals(driver.getId())) {

        throw new UnAuthorizedRideException(
                "You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.ACCEPTED) {
        throw new InvalidRideStateException(
                "Ride cannot be started");
    }

    var rider = ride.getRider();
    if (rider == null || rider.getRidePin() == null || !rider.getRidePin().equals(ridePin)) {
        throw new InvalidRideStateException(
                "Invalid ride PIN");
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
                    new ResourceNotFoundException("Ride not found"));

    if (ride.getDriver() == null ||
            !ride.getDriver().getId().equals(driver.getId())) {

        throw new UnAuthorizedRideException(
                "You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.STARTED) {
        throw new InvalidRideStateException(
                "Ride cannot be completed");
    }

    ride.setStatus(RideStatus.COMPLETED);

    setDriverAvailability(driver.getId(), true);

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

    public String requestCancellation(UUID rideId, User driver) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Ride not found"));

    if (ride.getDriver() == null ||
            !ride.getDriver().getId().equals(driver.getId())) {

        throw new UnAuthorizedRideException(
                "You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.ACCEPTED) {
        throw new InvalidRideStateException(
                "Driver cancellation is allowed only after acceptance");
    }

    String otp = String.format(
            "%04d",
            new SecureRandom().nextInt(10000)
    );

    ride.setCancellationOtp(otp);
    ride.setCancellationOtpExpiry(
            Instant.now().plus(5, ChronoUnit.MINUTES)
    );

    rideRepository.save(ride);

    return otp;
}

    public RideResponse cancelRideByDriver(
        UUID rideId,
        User driver,
        String cancellationOtp) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Ride not found"));

    if (ride.getDriver() == null ||
            !ride.getDriver().getId().equals(driver.getId())) {

        throw new UnAuthorizedRideException(
                "You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.ACCEPTED) {
        throw new InvalidRideStateException(
                "Driver can cancel only an accepted ride");
    }

    if (ride.getCancellationOtp() == null ||
            !ride.getCancellationOtp().equals(cancellationOtp)) {

        throw new InvalidRideStateException(
                "Invalid cancellation OTP");
    }

    if (ride.getCancellationOtpExpiry() == null ||
            Instant.now().isAfter(ride.getCancellationOtpExpiry())) {

        throw new InvalidRideStateException(
                "Cancellation OTP has expired");
    }

    ride.setStatus(RideStatus.CANCELLED);

    setDriverAvailability(driver.getId(), true);

    // OTP is single-use
    ride.setCancellationOtp(null);
    ride.setCancellationOtpExpiry(null);

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

   private void setDriverAvailability(UUID driverId, boolean available) {

    var driverProfile = driverProfileRepository
            .findByUserId(driverId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Driver profile not found"));

    driverProfile.setAvailable(available);
    driverProfileRepository.save(driverProfile);
}

   public void updateLocation(
        UUID userId,
        Double latitude,
        Double longitude) {

    var driverProfile = driverProfileRepository
            .findByUserId(userId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Driver profile not found"));

    driverProfile.setLatitude(latitude);
    driverProfile.setLongitude(longitude);

    driverProfileRepository.save(driverProfile);
}

   public List<DriverProfile> getNearbyDrivers(
        double pickupLatitude,
        double pickupLongitude,
        double radiusKm) {

    List<DriverProfile> availableDrivers =
            driverProfileRepository.findByAvailableTrue();

    return availableDrivers.stream()
            .filter(driver -> driver.getLatitude() != null)
            .filter(driver -> driver.getLongitude() != null)
            .filter(driver -> {

                double distance = locationService.calculateDistance(
                        pickupLatitude,
                        pickupLongitude,
                        driver.getLatitude(),
                        driver.getLongitude()
                );

                return distance <= radiusKm;
            })
            .toList();
}


}
  