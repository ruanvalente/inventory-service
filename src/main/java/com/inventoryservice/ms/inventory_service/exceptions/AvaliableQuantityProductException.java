package com.inventoryservice.ms.inventory_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AvaliableQuantityProductException extends RuntimeException {
  public AvaliableQuantityProductException() {
    super("A quantidade disponível não pode ser negativa.");
  }
}