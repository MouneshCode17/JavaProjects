package com.app.dev.bikeapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.dev.bikeapp.entity.Ride;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {
    
}
