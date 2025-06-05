package com.inventoryservice.ms.inventory_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.inventoryservice.ms.inventory_service.exceptions.entities.ApiError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ApiError> handleProductNotFound(
      ProductNotFoundException ex,
      HttpServletRequest request) {
    ApiError error = new ApiError(
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        ex.getMessage(),
        request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ApiError> handleInsufficientStock(
      InsufficientStockException ex,
      HttpServletRequest request) {
    ApiError error = new ApiError(
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        ex.getMessage(),
        request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @SuppressWarnings("null")
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {

    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((msg1, msg2) -> msg1 + "; " + msg2)
        .orElse("Parâmetros obrigatórios ausentes ou inválidos.");

    ApiError error = new ApiError(
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        message,
        request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}
