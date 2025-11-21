package com.example.pricing_service.service;

import com.example.pricing_service.dto.CreatePolicyRequest;
import com.example.pricing_service.dto.FareResponse;
import com.example.pricing_service.dto.PolicyResponse;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final FarePolicyRepository farePolicyRepository;

    @Transactional(readOnly = true)
    public FareResponse calculateFare(Integer distanceMeters, Integer durationSeconds, LocalDateTime endTimestamp) {
        FarePolicy policy = farePolicyRepository.findByIsActiveTrue()
                                                .orElseThrow(() -> new NoActiveFarePolicyException("현재 활성화된 요금 정책이 없습니다."));

        log.info("요금 계산 시작. Policy: {}", policy.getPolicyName());

        BigDecimal finalFare = new BigDecimal(policy.getBaseFare());

        // 1. 거리 비례 요금 (기본 거리 초과 시)
        if (distanceMeters > policy.getBaseDistance()) {
            BigDecimal extraDistance = new BigDecimal(distanceMeters - policy.getBaseDistance());
            BigDecimal distanceFare = extraDistance.multiply(policy.getRatePerMeter());
            finalFare = finalFare.add(distanceFare);
        }

        // 2. 시간 비례 요금
        BigDecimal durationFare = new BigDecimal(durationSeconds).multiply(policy.getRatePerSecond());
        finalFare = finalFare.add(durationFare);

        // 3. 할증 적용 (심야/출퇴근 등)
        if (isSurchargeApplicable(policy, endTimestamp.toLocalTime())) {
            finalFare = finalFare.multiply(policy.getSurchargeRate());
            log.info("할증 적용됨. Rate: {}", policy.getSurchargeRate());
        }

        // 4. 최종 금액 반올림 (1원 단위)
        int calculatedFare = finalFare.setScale(0, RoundingMode.HALF_UP).intValue();
        log.info("최종 요금 계산 완료: {}원", calculatedFare);

        return new FareResponse(calculatedFare);
    }

    @Transactional
    public PolicyResponse createFarePolicy(CreatePolicyRequest request) {

        if (request.isActive()) {
            farePolicyRepository.deactivateAllPolicies();
        }

        FarePolicy newPolicy = FarePolicy.builder()
                                         .policyName(request.policyName())
                                         .baseFare(request.baseFare())
                                         .baseDistance(request.baseDistance())
                                         .ratePerMeter(request.ratePerMeter())
                                         .ratePerSecond(request.ratePerSecond())
                                         .surchargeStartTime(request.surchargeStartTime())
                                         .surchargeEndTime(request.surchargeEndTime())
                                         .surchargeRate(request.surchargeRate())
                                         .isActive(request.isActive())
                                         .build();

        FarePolicy savedPolicy = farePolicyRepository.save(newPolicy);
        log.info("새로운 요금 정책 생성 완료. Policy ID: {}", savedPolicy.getId());
        return PolicyResponse.fromEntity(savedPolicy);
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> getAllFarePolicies() {
        return farePolicyRepository.findAll().stream()
                                   .map(PolicyResponse::fromEntity)
                                   .collect(Collectors.toList());
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
        else {
            return !endTime.isBefore(start) && endTime.isBefore(end);
        }
    }
}