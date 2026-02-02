package com.logistics.fleet.mapper;

import com.logistics.fleet.model.Vehicle;
import com.logistics.fleet.model.VehicleType;
import com.logistics.platform.common.dto.fleet.VehicleDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {
    VehicleDto toDto(Vehicle vehicle);
    Vehicle toEntity(VehicleDto vehicleDto);
}
