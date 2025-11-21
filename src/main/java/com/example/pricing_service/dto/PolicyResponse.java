package com.example.pricing_service.dto;

import com.example.pricing_service.entity.FarePolicy;
import java.math.BigDecimal;
import java.time.LocalTime;

public record PolicyResponse(
        Long id,
        String policyName,
        Integer baseFare,
        Integer baseDistance,
        BigDecimal ratePerMeter,
        BigDecimal ratePerSecond,
        LocalTime surchargeStartTime,
        LocalTime surchargeEndTime,
        BigDecimal surchargeRate,
        boolean isActive
) {
    public static PolicyResponse fromEntity(FarePolicy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getPolicyName(),
                policy.getBaseFare(),
                policy.getBaseDistance(),
                policy.getRatePerMeter(),
                policy.getRatePerSecond(),
                policy.getSurchargeStartTime(),
                policy.getSurchargeEndTime(),
                policy.getSurchargeRate(),
                policy.isActive()
        );
    }
}