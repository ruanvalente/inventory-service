package com.inventoryservice.ms.inventory_service.messaging;

import java.time.Instant;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.inventoryservice.ms.inventory_service.entities.dto.RabbitMQMessageDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.OrderRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.response.ErrorMessageDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.response.InventoryResponseDTO;
import com.inventoryservice.ms.inventory_service.entities.enums.ErrorType;
import com.inventoryservice.ms.inventory_service.entities.enums.InventoryStatus;
import com.inventoryservice.ms.inventory_service.exceptions.InsufficientStockException;
import com.inventoryservice.ms.inventory_service.exceptions.ProductNotFoundException;
import com.inventoryservice.ms.inventory_service.services.ProductService;

@Component
public class InventoryListener {

  private final ProductService productService;
  private final RabbitTemplate rabbitTemplate;

  public InventoryListener(ProductService productService, RabbitTemplate rabbitTemplate) {
    this.productService = productService;
    this.rabbitTemplate = rabbitTemplate;
  }

  @RabbitListener(queues = "inventory-queue")
  public InventoryResponseDTO handleOrderCreated(RabbitMQMessageDTO rabbitmqMessage) {
    try {
      OrderRequestDTO orderRequest = rabbitmqMessage.data();

      for (var item : orderRequest.items()) {
        InventoryResponseDTO response = productService.validateOrderItem(item);
        if (response.status() == InventoryStatus.ERROR) {
          publishErrorToQueue(ErrorType.VALIDATION_ERROR, response.message(), orderRequest);
          return response;
        }
      }

      return new InventoryResponseDTO(
          InventoryStatus.SUCCESS,
          "Todos os itens do pedido foram validados com sucesso",
          null);

    } catch (ProductNotFoundException e) {
      publishErrorToQueue(ErrorType.PRODUCT_NOT_FOUND, e.getMessage(), rabbitmqMessage.data());
      return new InventoryResponseDTO(
          InventoryStatus.ERROR,
          e.getMessage(),
          null);
    } catch (InsufficientStockException e) {
      publishErrorToQueue(ErrorType.INSUFFICIENT_STOCK, e.getMessage(), rabbitmqMessage.data());
      return new InventoryResponseDTO(
          InventoryStatus.ERROR,
          e.getMessage(),
          null);
    } catch (Exception e) {
      publishErrorToQueue(ErrorType.GENERIC_ERROR, "Não foi possível processar o pedido: " + e.getMessage(),
          rabbitmqMessage.data());
      return new InventoryResponseDTO(
          InventoryStatus.ERROR,
          "Não foi possível processar o pedido no momento. Tente novamente mais tarde: " + e.getMessage(),
          null);
    }
  }

  private void publishErrorToQueue(ErrorType errorType, String message, OrderRequestDTO orderRequest) {
    ErrorMessageDTO errorMessage = new ErrorMessageDTO(
        errorType,
        message,
        orderRequest,
        Instant.now());
    rabbitTemplate.convertAndSend("error-exchange", "error-routing-key", errorMessage);
    System.out.println("Published error to error-queue: " + errorMessage);
  }
}