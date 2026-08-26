package com.app.dev.bikeapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.dev.bikeapp.entity.DriverProfile;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, UUID>{
     Optional<DriverProfile> findByUserId(UUID userId);
}
