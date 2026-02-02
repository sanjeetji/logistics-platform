package com.logistics.order.mapper;

import com.logistics.order.model.TransportOrder;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    TransportOrderDto toDto(TransportOrder order);
    TransportOrder toEntity(TransportOrderDto orderDto);
}
