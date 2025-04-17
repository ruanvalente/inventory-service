package com.inventoryservice.ms.inventory_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

  @RabbitListener(queues = "inventory-queue")
  public void handleOrderCreated(String message) {
    System.out.println("Order received " + message);
  }
}
