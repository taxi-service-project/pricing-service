package com.example.pricing_service.service;

import com.example.pricing_service.dto.FareResponse;
import com.example.pricing_service.entity.FarePolicy;
import com.example.pricing_service.exception.NoActiveFarePolicyException;
import com.example.pricing_service.repository.FarePolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PricingService {

    private final FarePolicyRepository farePolicyRepository;

    public FareResponse calculateFare(Integer distanceMeters, Integer durationSeconds, LocalDateTime endTimestamp) {
        FarePolicy policy = farePolicyRepository.findByIsActiveTrue()
                                                .orElseThrow(() -> new NoActiveFarePolicyException("현재 활성화된 요금 정책이 없습니다."));

        log.info("요금 계산 시작. Policy: {}", policy.getPolicyName());

        BigDecimal finalFare = new BigDecimal(policy.getBaseFare());

        if (distanceMeters > policy.getBaseDistance()) {
            BigDecimal extraDistance = new BigDecimal(distanceMeters - policy.getBaseDistance());
            BigDecimal distanceFare = extraDistance.multiply(policy.getRatePerMeter());
            finalFare = finalFare.add(distanceFare);
        }

        BigDecimal durationFare = new BigDecimal(durationSeconds).multiply(policy.getRatePerSecond());
        finalFare = finalFare.add(durationFare);

        if (isSurchargeApplicable(policy, endTimestamp.toLocalTime())) {
            finalFare = finalFare.multiply(policy.getSurchargeRate());
            log.info("심야 할증 적용. Rate: {}", policy.getSurchargeRate());
        }

        int calculatedFare = finalFare.setScale(0, RoundingMode.HALF_UP).intValue();
        log.info("최종 요금 계산 완료: {}원", calculatedFare);

        return new FareResponse(calculatedFare);
    }

    private boolean isSurchargeApplicable(FarePolicy policy, LocalTime endTime) {
        if (policy.getSurchargeStartTime() == null || policy.getSurchargeEndTime() == null) {
            return false;
        }
        LocalTime start = policy.getSurchargeStartTime();
        LocalTime end = policy.getSurchargeEndTime();

        if (start.isAfter(end)) {
            return endTime.isAfter(start) || endTime.isBefore(end);
        }
        return false;
    }
}