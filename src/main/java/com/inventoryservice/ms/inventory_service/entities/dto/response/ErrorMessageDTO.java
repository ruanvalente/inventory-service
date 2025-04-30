package com.inventoryservice.ms.inventory_service.entities.dto.response;

import java.time.Instant;

import com.inventoryservice.ms.inventory_service.entities.dto.request.OrderRequestDTO;

public record ErrorMessageDTO(
    String errorType, // "PRODUCT_NOT_FOUND", "INSUFFICIENT_STOCK", "GENERIC_ERROR"
    String message,
    OrderRequestDTO orderRequest,
    Instant timestamp) {

}
