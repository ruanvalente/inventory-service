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
import com.inventoryservice.ms.inventory_service.entities.dto.request.UpdateQuantityDTO;
import com.inventoryservice.ms.inventory_service.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Products", description = "Product management operations")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @Operation(summary = "List all products with pagination and sorting")
  @GetMapping
  public ResponseEntity<Page<Product>> listAll(
      @Parameter(description = "Pagination configuration and Sorting", example = "{\"page\":0,\"size\":10,\"sort\":[\"\"]}") @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<Product> products = this.productService.listAll(pageable);

    if (products.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(products);
  }

  @Operation(summary = "Find a product by ID")
  @GetMapping("/{id}")
  public ResponseEntity<Product> findById(@Parameter(description = "Product ID") @PathVariable Long id) {
    return ResponseEntity.ok(productService.findById(id));
  }

  @Operation(summary = "Create a new product")
  @PostMapping
  public ResponseEntity<Product> create(@Validated @RequestBody ProductRequestDTO productDTO) {
    Product createdProduct = productService.create(productDTO);
    return ResponseEntity.status(201).body(createdProduct);
  }

  @Operation(summary = "Update an existing product")
  @PutMapping("/{id}")
  public ResponseEntity<Product> update(
      @Parameter(description = "Product ID") @PathVariable Long id,
      @Validated @RequestBody ProductUpdateRequestDTO productDTO) {
    Product updatedProduct = productService.update(id, productDTO);
    return ResponseEntity.ok(updatedProduct);
  }

  @Operation(summary = "Update product quantity by ID")
  @PatchMapping("/{id}/quantity")
  public ResponseEntity<Product> updateQuantity(@Parameter(description = "Product ID") @PathVariable Long id,
      @RequestBody UpdateQuantityDTO dto) {
    Product updatedProduct = productService.updateQuantity(id, dto.availableQuantity());
    return ResponseEntity.ok(updatedProduct);
  }

  @Operation(summary = "Remove a product by ID")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@Parameter(description = "Product ID") @PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
