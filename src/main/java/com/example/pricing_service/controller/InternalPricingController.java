package com.example.pricing_service.controller;

import com.example.pricing_service.dto.FareResponse;
import com.example.pricing_service.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/internal/api/pricing")
@RequiredArgsConstructor
public class InternalPricingController {

    private final PricingService pricingService;

    @GetMapping("/calculate")
    public ResponseEntity<FareResponse> calculateFare(
            @RequestParam("distance_meters") Integer distanceMeters,
            @RequestParam("duration_seconds") Integer durationSeconds,
            @RequestParam("end_timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp) {
        FareResponse response = pricingService.calculateFare(distanceMeters, durationSeconds, endTimestamp);
        return ResponseEntity.ok(response);
    }
}