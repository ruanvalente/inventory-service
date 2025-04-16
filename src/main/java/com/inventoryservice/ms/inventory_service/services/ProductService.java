package com.inventoryservice.ms.inventory_service.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inventoryservice.ms.inventory_service.entities.Product;
import com.inventoryservice.ms.inventory_service.exceptions.ProductNotFoundException;
import com.inventoryservice.ms.inventory_service.repositories.ProductRepository;

@Service
public class ProductService {
  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<Product> listAll() {
    return this.productRepository.findAll();
  }

  public Product findById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }
}
