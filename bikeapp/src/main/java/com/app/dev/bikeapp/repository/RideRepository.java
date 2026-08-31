package com.app.dev.bikeapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.dev.bikeapp.dto.RideStatus;
import com.app.dev.bikeapp.entity.Ride;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface RideRepository extends JpaRepository<Ride, UUID> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ride> findById(UUID rideId);

    List<Ride> findByStatus(RideStatus status);

    Optional<Ride> findFirstByRiderIdAndStatusIn(
        UUID riderId,
        List<RideStatus> statuses
);
    Optional<Ride> findFirstByDriverIdAndStatusIn(
        UUID driverId,
        List<RideStatus> statuses
);

}
