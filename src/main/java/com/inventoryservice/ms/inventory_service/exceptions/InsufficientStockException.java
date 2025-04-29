package com.inventoryservice.ms.inventory_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {
  public InsufficientStockException(Long productId, int requestedQuantity, int availableQuantity) {
    super("Estoque insuficiente para o produto ID " + productId +
        ". Quantidade solicitada: " + requestedQuantity +
        ", Quantidade disponível: " + availableQuantity);
  }
}