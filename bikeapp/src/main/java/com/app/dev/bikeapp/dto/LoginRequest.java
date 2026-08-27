package com.app.dev.bikeapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

    @NotBlank
    @Pattern(
    regexp = "^[6-9]\\d{9}$",
    message = "Phone number must be a valid 10-digit Indian mobile number"
    )   
    private String phoneNumber;

    @NotBlank
    private String password;
}