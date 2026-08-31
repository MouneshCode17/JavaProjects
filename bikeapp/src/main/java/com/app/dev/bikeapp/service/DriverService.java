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
import com.app.dev.bikeapp.dto.RideStatus;
import com.app.dev.bikeapp.dto.DriverInfoResponse;

import com.app.dev.bikeapp.exception.ResourceNotFoundException;
import com.app.dev.bikeapp.exception.UnAuthorizedRideException;
import com.app.dev.bikeapp.exception.InvalidRideStateException;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;
    private final RideRepository rideRepository;
    private final LocationService locationService;

    @Transactional
    public void updateAvailability(
        UUID userId,
        boolean available) {


    var driverProfile = driverProfileRepository
            .findByUserId(userId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Driver profile not found"));

     if (available &&
        (driverProfile.getLatitude() == null ||
         driverProfile.getLongitude() == null)) {

    throw new InvalidRideStateException(
            "Update your location before going online");
        }         
        
     if (available) {

    var activeRide = rideRepository.findFirstByDriverIdAndStatusIn(
            userId,
            List.of(
                    RideStatus.ACCEPTED,
                    RideStatus.ARRIVED,
                    RideStatus.STARTED
            )
    );

    if (activeRide.isPresent()) {
        throw new InvalidRideStateException(
                "Cannot go online while you have an active ride");
    }
}

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

    var driverProfile = driverProfileRepository
            .findByUserId(driver.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Driver profile not found"));

   if(!driverProfile.isAvailable()) {
        throw new InvalidRideStateException(
                "Driver is not available to accept rides");
    }

    ride.setDriver(driver);

    ride.setStatus(RideStatus.ACCEPTED);

    setDriverAvailability(driver.getId(), false);
        
    Ride savedRide = rideRepository.save(ride);

    return toRideResponse(savedRide);
}

    @Transactional
    public RideResponse startRide(UUID rideId, User driver, String ridePin) {

    Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Ride not found"));

    if (ride.getDriver() == null ||
            !ride.getDriver().getId().equals(driver.getId())) {

        throw new UnAuthorizedRideException(
                "You are not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.ARRIVED) {
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

    return toRideResponse(savedRide);
}

    @Transactional
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

    return toRideResponse(savedRide);
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

    @Transactional
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

    ride.setDriver(null);

    ride.setStatus(RideStatus.REQUESTED);

    setDriverAvailability(driver.getId(), true);

    // OTP is single-use
    ride.setCancellationOtp(null);
    ride.setCancellationOtpExpiry(null);

    Ride savedRide = rideRepository.save(ride);

    return toRideResponse(savedRide);
}

   private void setDriverAvailability(UUID driverId, boolean available) {

    var driverProfile = driverProfileRepository
            .findByUserId(driverId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Driver profile not found"));

    driverProfile.setAvailable(available);
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
   
   public List<Ride> getRideRequests() {
    return rideRepository.findByStatus(RideStatus.REQUESTED);
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

  @Transactional
  public RideResponse arriveRide(UUID rideId, User driver) {

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
                "Driver cannot mark ride as arrived");
    }

    ride.setStatus(RideStatus.ARRIVED);

    Ride savedRide = rideRepository.save(ride);

    return toRideResponse(savedRide);
}

  @Transactional
public void updateLocation(
        UUID driverId,
        double latitude,
        double longitude) {

    var driverProfile = driverProfileRepository
            .findByUserId(driverId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Driver profile not found"));

        if (latitude < -90 || latitude > 90) {
    throw new InvalidRideStateException("Invalid latitude");
        }

        if (longitude < -180 || longitude > 180) {
    throw new InvalidRideStateException("Invalid longitude");
        }

    driverProfile.setLatitude(latitude);
    driverProfile.setLongitude(longitude);

    driverProfileRepository.save(driverProfile);
}
}
  