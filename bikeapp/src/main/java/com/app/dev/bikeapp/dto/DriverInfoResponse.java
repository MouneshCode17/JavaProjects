package com.app.dev.bikeapp.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DriverInfoResponse {

    private UUID driverId;
    private String name;
    private String phoneNumber;
}