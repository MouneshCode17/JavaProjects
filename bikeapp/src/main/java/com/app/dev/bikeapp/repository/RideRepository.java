package com.app.dev.bikeapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.dev.bikeapp.entity.Ride;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface RideRepository extends JpaRepository<Ride, UUID> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ride> findById(UUID rideId);
}
