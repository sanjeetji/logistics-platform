package com.logistics.parcel.service;

import com.logistics.parcel.adapter.PartnerAdapter;
import com.logistics.parcel.model.Parcel;
import com.logistics.parcel.model.Partner;
import com.logistics.parcel.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerAggregatorService {

    private final List<PartnerAdapter> adapters;
    private final PartnerRepository partnerRepository;

    public Map<String, BigDecimal> getAllRates(Parcel parcel) {
        return adapters.stream()
                .collect(Collectors.toMap(
                        PartnerAdapter::getPartnerName,
                        adapter -> adapter.getRate(parcel)));
    }

    public PartnerAdapter selectBestPartner(Parcel parcel) {
        List<Partner> activePartners = partnerRepository.findByStatus("ACTIVE");
        List<String> activeNames = activePartners.stream()
                .map(Partner::getName)
                .map(String::toUpperCase)
                .toList();

        return adapters.stream()
                .filter(adapter -> activeNames.contains(adapter.getPartnerName().toUpperCase()))
                .min(Comparator.comparing(adapter -> adapter.getRate(parcel)))
                .orElse(adapters.get(0)); // Fallback to first adapter if no match or active found
    }

    public String createShipmentWithBestPartner(Parcel parcel) {
        PartnerAdapter bestAdapter = selectBestPartner(parcel);
        log.info("Selected partner: {} for parcel: {}", bestAdapter.getPartnerName(), parcel.getTrackingNumber());

        // Find partner entity to get ID
        partnerRepository.findByStatus("ACTIVE").stream()
                .filter(p -> p.getName().equalsIgnoreCase(bestAdapter.getPartnerName()))
                .findFirst()
                .ifPresent(p -> parcel.setPartnerId(p.getId()));

        return bestAdapter.createShipment(parcel);
    }
}
