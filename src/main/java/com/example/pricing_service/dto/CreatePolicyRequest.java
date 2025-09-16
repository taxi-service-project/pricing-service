package com.example.pricing_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalTime;

public record CreatePolicyRequest(
        @NotBlank String policyName,
        @NotNull @PositiveOrZero Integer baseFare,
        @NotNull @PositiveOrZero Integer baseDistance,
        @NotNull @PositiveOrZero BigDecimal ratePerMeter,
        @NotNull @PositiveOrZero BigDecimal ratePerSecond,
        LocalTime surchargeStartTime,
        LocalTime surchargeEndTime,
        @Positive BigDecimal surchargeRate,
        @NotNull Boolean isActive
) {}