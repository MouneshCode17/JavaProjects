package com.app.dev.bikeapp.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import com.app.dev.bikeapp.dto.DriverInfoResponse;


public record RideResponse(
        UUID id,
        UUID riderId,
        UUID driverId,
        String pickupLocation,
        String dropLocation,
        RideStatus status,
        BigDecimal fare,
        Instant createdAt,
        Instant updatedAt,
        BigDecimal cancellationFee,
        DriverInfoResponse driverInfo
) {
}