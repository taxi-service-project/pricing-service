package com.example.pricing_service.repository;

import com.example.pricing_service.entity.FarePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FarePolicyRepository extends JpaRepository<FarePolicy, Long> {
    Optional<FarePolicy> findByIsActiveTrue();
}