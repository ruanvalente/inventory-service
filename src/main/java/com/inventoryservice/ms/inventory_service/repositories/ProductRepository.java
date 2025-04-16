package com.inventoryservice.ms.inventory_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventoryservice.ms.inventory_service.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
