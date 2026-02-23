package com.logistics.dispatch.mapper;

import com.logistics.dispatch.dto.DispatchJobDto;
import com.logistics.dispatch.model.DispatchJob;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface DispatchMapper {
    // Mapper for Dispatch entities

    DispatchJobDto toDto(DispatchJob job);

    DispatchJob toEntity(DispatchJobDto jobDto);
}
