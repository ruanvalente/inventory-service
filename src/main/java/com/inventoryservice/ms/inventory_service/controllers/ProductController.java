package com.inventoryservice.ms.inventory_service.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.inventoryservice.ms.inventory_service.entities.Product;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductUpdateRequestDTO;
import com.inventoryservice.ms.inventory_service.services.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<Page<Product>> listAll(
    @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<Product> products = this.productService.listAll(pageable);

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

  @PutMapping("/{id}")
  public ResponseEntity<Product> update(
    @PathVariable Long id, 
    @Validated @RequestBody ProductUpdateRequestDTO productDTO) {
    Product updatedProduct = productService.update(id, productDTO);
    return ResponseEntity.ok(updatedProduct);
  }
}
