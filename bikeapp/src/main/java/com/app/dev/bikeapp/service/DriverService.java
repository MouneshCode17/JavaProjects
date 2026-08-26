package com.app.dev.bikeapp.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.dev.bikeapp.repository.DriverProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;

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
}
