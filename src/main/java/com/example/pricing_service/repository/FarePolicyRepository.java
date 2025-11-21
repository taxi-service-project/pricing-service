package com.example.pricing_service.repository;

import com.example.pricing_service.entity.FarePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FarePolicyRepository extends JpaRepository<FarePolicy, Long> {
    Optional<FarePolicy> findByIsActiveTrue();

    @Modifying
    @Query("UPDATE FarePolicy p SET p.isActive = false WHERE p.isActive = true")
    void deactivateAllPolicies();
}