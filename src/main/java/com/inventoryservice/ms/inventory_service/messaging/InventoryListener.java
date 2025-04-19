package com.inventoryservice.ms.inventory_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.inventoryservice.ms.inventory_service.entities.dto.RabbitMQMessageDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.OrderRequestDTO;

@Component
public class InventoryListener {

  @RabbitListener(queues = "inventory-queue")
  public void handleOrderCreated(RabbitMQMessageDTO rabbitmqMessage) {
    try {
      System.out.println("Order received: Pattern: " + rabbitmqMessage.pattern());
      OrderRequestDTO orderRequest = rabbitmqMessage.data();
      System.out.println("Client ID: " + orderRequest.clientId());
      orderRequest.items()
          .forEach(item -> {
            System.out.println("ID: " + item.productId());
            System.out.println("UnitPrice: " + item.unitPrice());
            System.out.println("Quantity: " + item.quantity());
          });
    } catch (Exception e) {
      System.err.println("Error processing order: " + e.getMessage());
    }
  }
}
