package com.inventoryservice.ms.inventory_service.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryservice.ms.inventory_service.entities.Product;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductRequestDTO;
import com.inventoryservice.ms.inventory_service.services.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<List<Product>> listAll() {
    List<Product> products = this.productService.listAll();

    if (products.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> findById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.findById(id));
  }

  @PostMapping
  public ResponseEntity<Product> create(@Validated @RequestBody ProductRequestDTO productDTO) {
    Product createdProduct = productService.create(productDTO);
    return ResponseEntity.status(201).body(createdProduct);
  }
}
