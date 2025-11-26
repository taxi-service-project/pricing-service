package com.example.pricing_service.controller;

import com.example.pricing_service.dto.CreatePolicyRequest;
import com.example.pricing_service.dto.FareResponse;
import com.example.pricing_service.dto.PolicyResponse;
import com.example.pricing_service.service.PricingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalPricingController.class)
class InternalPricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PricingService pricingService;

    @Test
    @DisplayName("요금 계산 API: 필수 파라미터가 정상적이면 200 OK와 계산된 요금을 반환한다")
    void calculateFare_Success() throws Exception {
        // Given
        int distance = 5000;
        int duration = 600;
        String endTimestampStr = "2025-11-26T10:00:00";
        LocalDateTime endTimestamp = LocalDateTime.parse(endTimestampStr);

        FareResponse expectedResponse = new FareResponse(7500);

        given(pricingService.calculateFare(eq(distance), eq(duration), eq(endTimestamp)))
                .willReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/internal/api/pricing/calculate")
                       .param("distance_meters", String.valueOf(distance))
                       .param("duration_seconds", String.valueOf(duration))
                       .param("end_timestamp", endTimestampStr)
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.fare").value(7500));

        then(pricingService).should(times(1)).calculateFare(eq(distance), eq(duration), eq(endTimestamp));
    }

    @Test
    @DisplayName("정책 생성 API: 정상적인 요청 시 201 Created와 생성된 정책을 반환한다")
    void createFarePolicy_Success() throws Exception {
        // Given
        CreatePolicyRequest request = new CreatePolicyRequest(
                "Standard Policy",
                4800,
                1600,
                new BigDecimal("1.5"),
                new BigDecimal("0.5"),
                LocalTime.of(22, 0),
                LocalTime.of(4, 0),
                new BigDecimal("1.2"),
                true
        );

        PolicyResponse response = new PolicyResponse(
                1L,
                "Standard Policy",
                4800,
                1600,
                new BigDecimal("1.5"),
                new BigDecimal("0.5"),
                LocalTime.of(22, 0),
                LocalTime.of(4, 0),
                new BigDecimal("1.2"),
                true
        );

        given(pricingService.createFarePolicy(any(CreatePolicyRequest.class)))
                .willReturn(response);

        // When & Then
        mockMvc.perform(post("/internal/api/pricing/policies")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.policyName").value("Standard Policy"))
               .andExpect(jsonPath("$.baseFare").value(4800))
               .andExpect(jsonPath("$.ratePerMeter").value(1.5));
    }

    @Test
    @DisplayName("정책 조회 API: 모든 정책 목록을 반환한다")
    void getAllFarePolicies_Success() throws Exception {
        // Given
        PolicyResponse policy1 = new PolicyResponse(
                1L, "Policy A", 3000, 1000,
                BigDecimal.ONE, BigDecimal.ZERO, null, null, BigDecimal.ONE, true
        );
        PolicyResponse policy2 = new PolicyResponse(
                2L, "Policy B", 4000, 2000,
                BigDecimal.TEN, BigDecimal.ONE, null, null, BigDecimal.ONE, false
        );

        given(pricingService.getAllFarePolicies())
                .willReturn(List.of(policy1, policy2));

        // When & Then
        mockMvc.perform(get("/internal/api/pricing/policies")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()").value(2))
               .andExpect(jsonPath("$[0].policyName").value("Policy A"))
               .andExpect(jsonPath("$[0].baseFare").value(3000))
               .andExpect(jsonPath("$[1].policyName").value("Policy B"));
    }
}