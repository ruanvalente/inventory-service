package com.inventoryservice.ms.inventory_service.entities.dto.response;

import java.time.Instant;

import com.inventoryservice.ms.inventory_service.entities.dto.request.OrderRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.enums.ErrorType;

public record ErrorMessageDTO(
        ErrorType errorType,
        String message,
        OrderRequestDTO orderRequest,
        Instant timestamp) {

}
