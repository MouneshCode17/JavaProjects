package com.app.dev.bikeapp.dto;

import java.util.UUID;

public record DriverResponse(
        UUID id,
        UUID userId,
        String name,
        boolean available
) {
}