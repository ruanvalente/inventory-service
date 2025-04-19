package com.inventoryservice.ms.inventory_service.entities.dto;

public record CreateOrderItemDTO(
    Long productId,
    Integer quantity,
    Double unitPrice) {
}
