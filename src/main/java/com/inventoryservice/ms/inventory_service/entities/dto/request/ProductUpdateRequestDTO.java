package com.inventoryservice.ms.inventory_service.entities.dto.request;

public record ProductUpdateRequestDTO(
        String name,
        String description,
        Double price) {

}
