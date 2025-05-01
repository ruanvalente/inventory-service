package com.inventoryservice.ms.inventory_service.entities.dto.response;

import com.inventoryservice.ms.inventory_service.entities.enums.InventoryStatus;

public record InventoryResponseDTO(
    InventoryStatus status,
    String message,
    Object data) {

  public InventoryResponseDTO {
    status = status == null ? InventoryStatus.SUCCESS : status;
    message = message == null ? "" : message;
  }
}
