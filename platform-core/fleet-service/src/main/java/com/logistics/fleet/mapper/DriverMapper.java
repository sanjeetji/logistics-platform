package com.logistics.fleet.mapper;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.DriverStatus;
import com.logistics.fleet.model.VerificationStatus;
import com.logistics.platform.common.dto.fleet.DriverDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DriverMapper {
    DriverDto toDto(com.logistics.fleet.model.Driver driver);

    com.logistics.fleet.model.Driver toEntity(DriverDto driverDto);
}
