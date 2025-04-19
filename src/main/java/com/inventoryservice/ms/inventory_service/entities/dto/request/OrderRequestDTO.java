package com.inventoryservice.ms.inventory_service.entities.dto.request;

import java.util.List;

import com.inventoryservice.ms.inventory_service.entities.dto.CreateOrderItemDTO;

public record OrderRequestDTO(
        Long clientId,
        List<CreateOrderItemDTO> items) {

}
