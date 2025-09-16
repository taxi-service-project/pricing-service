package com.example.pricing_service.controller;

import com.example.pricing_service.dto.CreatePolicyRequest;
import com.example.pricing_service.dto.FareResponse;
import com.example.pricing_service.dto.PolicyResponse;
import com.example.pricing_service.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping("/policies")
    public ResponseEntity<PolicyResponse> createFarePolicy(@Valid @RequestBody CreatePolicyRequest request) {
        PolicyResponse response = pricingService.createFarePolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/policies")
    public ResponseEntity<List<PolicyResponse>> getAllFarePolicies() {
        List<PolicyResponse> response = pricingService.getAllFarePolicies();
        return ResponseEntity.ok(response);
    }
}