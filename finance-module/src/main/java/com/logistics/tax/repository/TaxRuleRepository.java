package com.logistics.tax.repository;

import com.logistics.tax.entity.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxRuleRepository extends JpaRepository<TaxRule, Long> {

    Optional<TaxRule> findByCountryCodeAndIsActiveTrue(String countryCode);
}
