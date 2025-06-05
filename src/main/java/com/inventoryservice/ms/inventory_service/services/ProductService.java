package com.inventoryservice.ms.inventory_service.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.inventoryservice.ms.inventory_service.entities.Product;
import com.inventoryservice.ms.inventory_service.entities.dto.CreateOrderItemDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.response.InventoryResponseDTO;
import com.inventoryservice.ms.inventory_service.entities.enums.InventoryStatus;
import com.inventoryservice.ms.inventory_service.exceptions.AvaliableQuantityProductException;
import com.inventoryservice.ms.inventory_service.exceptions.ProductNotFoundException;
import com.inventoryservice.ms.inventory_service.repositories.ProductRepository;

@Service
public class ProductService {
  private final ProductRepository productRepository;
  private static final String PRODUCT_NOT_FOUND_MESSAGE = "Produto com ID %d não encontrado";
  private static final String INSUFFICIENT_STOCK_MESSAGE = "Estoque insuficiente para o produto ID %d. Quantidade solicitada: %d, Quantidade disponível: %d";
  private static final String VALIDATION_SUCCESS_MESSAGE = "Produto ID %d validado com sucesso";
  private static final String GENERIC_ERROR_MESSAGE = "Erro ao validar o pedido: %s. Tente novamente mais tarde.";

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

 public Page<Product> listAll(Pageable pageable) {
    return this.productRepository.findAll(pageable);
}

  public Product findById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  public InventoryResponseDTO validateOrderItem(CreateOrderItemDTO item) {
    try {
      Product product = findById(item.productId());

      if (isInsufficientStock(item.quantity(), product.getAvailableQuantity())) {
        return createErrorResponse(
            String.format(INSUFFICIENT_STOCK_MESSAGE,
                item.productId(),
                item.quantity(),
                product.getAvailableQuantity()));
      }

      return createSuccessResponse(
          String.format(VALIDATION_SUCCESS_MESSAGE, item.productId()),
          product);

    } catch (ProductNotFoundException e) {
      return createErrorResponse(
          String.format(PRODUCT_NOT_FOUND_MESSAGE, item.productId()));
    } catch (Exception e) {
      return createErrorResponse(
          String.format(GENERIC_ERROR_MESSAGE, e.getMessage()));
    }
  }

  private InventoryResponseDTO createSuccessResponse(String message, Object data) {
    return new InventoryResponseDTO(InventoryStatus.SUCCESS, message, data);
  }

  private boolean isInsufficientStock(int requestedQuantity, int availableQuantity) {
    return requestedQuantity > availableQuantity;
  }

  private InventoryResponseDTO createErrorResponse(String message) {
    return new InventoryResponseDTO(InventoryStatus.ERROR, message, null);
  }

  public Product create(ProductRequestDTO productDTO) {
      if (productDTO.availableQuantity() <= 0) {
          throw new AvaliableQuantityProductException();
      }
      Product product = new Product();
      product.setName(productDTO.name());
      product.setDescription(productDTO.description());
      product.setAvailableQuantity(productDTO.availableQuantity());
      product.setPrice(productDTO.price());
      return this.productRepository.save(product);
  }
}
