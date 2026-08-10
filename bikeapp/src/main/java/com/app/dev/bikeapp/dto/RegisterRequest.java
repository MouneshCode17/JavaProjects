package com.app.dev.bikeapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
    
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
