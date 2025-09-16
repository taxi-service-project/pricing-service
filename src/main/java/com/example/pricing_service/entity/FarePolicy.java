package com.example.pricing_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "fare_policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FarePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String policyName;

    @Column(nullable = false)
    private Integer baseFare;

    @Column(nullable = false)
    private Integer baseDistance;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal ratePerMeter;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal ratePerSecond;

    private LocalTime surchargeStartTime;

    private LocalTime surchargeEndTime;

    @Column(precision = 3, scale = 2)
    private BigDecimal surchargeRate;

    @Column(nullable = false)
    private boolean isActive = true;

    @Builder
    public FarePolicy(String policyName, Integer baseFare, Integer baseDistance, BigDecimal ratePerMeter,
                      BigDecimal ratePerSecond, LocalTime surchargeStartTime, LocalTime surchargeEndTime,
                      BigDecimal surchargeRate, boolean isActive) {
        this.policyName = policyName;
        this.baseFare = baseFare;
        this.baseDistance = baseDistance;
        this.ratePerMeter = ratePerMeter;
        this.ratePerSecond = ratePerSecond;
        this.surchargeStartTime = surchargeStartTime;
        this.surchargeEndTime = surchargeEndTime;
        this.surchargeRate = surchargeRate;
        this.isActive = isActive;
    }
}