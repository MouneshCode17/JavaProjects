package com.app.dev.bikeapp.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import com.app.dev.bikeapp.entity.DriverProfile;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, UUID>{
     Optional<DriverProfile> findByUserId(UUID userId);

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     List<DriverProfile> findFirstByAvailableTrue();
}
