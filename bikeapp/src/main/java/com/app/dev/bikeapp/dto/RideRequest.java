package com.app.dev.bikeapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RideRequest {
    private String pickupLocation;
    private String dropoffLocation;
}