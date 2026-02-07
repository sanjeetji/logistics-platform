package com.logistics.pricing.mapper;

import com.logistics.pricing.dto.RateCardDto;
import com.logistics.pricing.model.RateCard;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RateCardMapper {
    RateCardDto toDto(RateCard rateCard);

    RateCard toEntity(RateCardDto rateCardDto);
}
