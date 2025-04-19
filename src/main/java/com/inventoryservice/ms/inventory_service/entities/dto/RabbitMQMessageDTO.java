package com.inventoryservice.ms.inventory_service.entities.dto;

import com.inventoryservice.ms.inventory_service.entities.dto.request.OrderRequestDTO;

public record RabbitMQMessageDTO(
    String pattern,
    OrderRequestDTO data) {

}
