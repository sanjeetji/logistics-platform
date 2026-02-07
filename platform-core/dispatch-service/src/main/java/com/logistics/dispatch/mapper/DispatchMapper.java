package com.logistics.dispatch.mapper;

import com.logistics.dispatch.dto.DispatchJobDto;
import com.logistics.dispatch.model.DispatchJob;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DispatchMapper {
    // Mapper for Dispatch entities

    DispatchJobDto toDto(DispatchJob job);

    DispatchJob toEntity(DispatchJobDto jobDto);
}
