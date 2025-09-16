package com.example.pricing_service.service;

import com.example.pricing_service.dto.FareResponse;
import com.example.pricing_service.entity.FarePolicy;
import com.example.pricing_service.repository.FarePolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @InjectMocks
    private PricingService pricingService;
    @Mock
    private FarePolicyRepository farePolicyRepository;

    @Test
    @DisplayName("기본 요금 계산 성공")
    void calculateFare_BaseRate_Success() {
        // given
        FarePolicy mockPolicy = FarePolicy.builder()
                                          .baseFare(3800).baseDistance(2000)
                                          .ratePerMeter(new BigDecimal("0.72")).ratePerSecond(new BigDecimal("0.25"))
                                          .build();
        when(farePolicyRepository.findByIsActiveTrue()).thenReturn(Optional.of(mockPolicy));

        // when (기본 거리/시간 이내)
        FareResponse response = pricingService.calculateFare(1500, 300, LocalDateTime.now());

        // then (거리 요금은 0, 시간 요금만 추가) = 3800 + (300 * 0.25) = 3875
        assertThat(response.fare()).isEqualTo(3875);
    }

    @Test
    @DisplayName("심야 할증 요금 계산 성공")
    void calculateFare_Surcharge_Success() {
        // given
        FarePolicy mockPolicy = FarePolicy.builder()
                                          .baseFare(4600).baseDistance(2000)
                                          .ratePerMeter(new BigDecimal("0.86")).ratePerSecond(new BigDecimal("0.3"))
                                          .surchargeStartTime(LocalTime.of(22, 0))
                                          .surchargeEndTime(LocalTime.of(4, 0))
                                          .surchargeRate(new BigDecimal("1.2")) // 20% 할증
                                          .build();
        when(farePolicyRepository.findByIsActiveTrue()).thenReturn(Optional.of(mockPolicy));

        // when (밤 11시에 운행 종료)
        LocalDateTime lateNight = LocalDateTime.of(2025, 9, 16, 23, 0);
        FareResponse response = pricingService.calculateFare(3000, 600, lateNight);

        // then
        // 기본요금(4600) + 거리요금(1000m * 0.86) + 시간요금(600s * 0.3) = 4600 + 860 + 180 = 5640
        // 할증 적용: 5640 * 1.2 = 6768
        assertThat(response.fare()).isEqualTo(6768);
    }
}