package com.inventoryservice.ms.inventory_service.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

  @Bean
  public Queue inventoryQueue() {
    return new Queue("inventory-queue", true);
  }

  @Bean
  public DirectExchange inventoryExchange() {
    return new DirectExchange("inventory-exchange");
  }

  @Bean
  public Binding bindingInventory() {
    return BindingBuilder
        .bind(inventoryQueue())
        .to(inventoryExchange())
        .with("inventory-routing-key");
  }
}
