package com.app.dev.bikeapp.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.app.dev.bikeapp.dto.RideStatus;

@Service
public class FareService {

    private static final BigDecimal BASE_FARE = BigDecimal.valueOf(30);
    private static final BigDecimal PER_KM_RATE = BigDecimal.valueOf(10);

    public BigDecimal calculateFare(double distanceKm) {

        return BASE_FARE.add(
                PER_KM_RATE.multiply(
                        BigDecimal.valueOf(distanceKm)
                )
        ).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateCancellationFee(
        RideStatus status,
        BigDecimal fare) {

    if (status == RideStatus.ACCEPTED) {
        return fare
                .multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
    }

    return BigDecimal.ZERO;
}
}