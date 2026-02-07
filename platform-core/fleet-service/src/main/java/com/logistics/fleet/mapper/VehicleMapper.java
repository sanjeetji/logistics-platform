package com.logistics.fleet.mapper;

import com.logistics.fleet.model.Vehicle;
import com.logistics.fleet.model.VehicleType;
import com.logistics.platform.common.dto.fleet.VehicleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {
    @Mapping(source = "currentDriver.id", target = "currentDriverId")
    VehicleDto toDto(Vehicle vehicle);

    @Mapping(target = "currentDriver", ignore = true) // Complex mapping, ignore for simple entity conversion or map id
                                                      // to entity
    @Mapping(target = "type", ignore = true) // String to Enum
    Vehicle toEntity(VehicleDto vehicleDto);
}
