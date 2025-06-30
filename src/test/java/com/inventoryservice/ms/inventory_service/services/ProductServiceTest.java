package com.inventoryservice.ms.inventory_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.inventoryservice.ms.inventory_service.entities.Product;
import com.inventoryservice.ms.inventory_service.entities.dto.CreateOrderItemDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductUpdateRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.response.InventoryResponseDTO;
import com.inventoryservice.ms.inventory_service.entities.enums.InventoryStatus;
import com.inventoryservice.ms.inventory_service.exceptions.AvaliableQuantityProductException;
import com.inventoryservice.ms.inventory_service.exceptions.ProductNotFoundException;
import com.inventoryservice.ms.inventory_service.repositories.ProductRepository;

public class ProductServiceTest {
    private Product product1;
    private Product product2;
    private ProductRepository productRepository;
    private ProductService productService;;

    @BeforeEach
    void setup() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);

        product1 = new Product(
                1L,
                "Caderno universitário",
                "Caderno universitário espiral 10 matérias, capa dura, 200 folhas",
                100,
                15.99, LocalDateTime.now());

        product2 = new Product(
                2L,
                "Caneta esferográfica",
                "Caneta esferográfica azul, ponta fina, corpo transparente",
                200,
                1.99, LocalDateTime.now());
    }

    @Test
    @DisplayName("Test getting all products")
    void testGetAllProducts() {
        List<Product> products = List.of(product1, product2);
        Pageable pageable = Pageable.unpaged();
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(products));

        Page<Product> result = productService.listAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Caderno universitário", result.getContent().get(0).getName());
        assertEquals("Caneta esferográfica", result.getContent().get(1).getName());
    }

    @Test
    @DisplayName("Test getting a product by ID")
    void testGetProductById() {
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals("Caderno universitário", result.getName());
        assertEquals("Caderno universitário espiral 10 matérias, capa dura, 200 folhas", result.getDescription());

    }

    @Test
    @DisplayName("Test getting a product by ID - Not Found")
    void testFindById_ProductNotFound() {
        when(productRepository
                .findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(2L));
    }

    @Test
    @DisplayName("Test creating a product")
    void testCreateProduct_Success() {
        ProductRequestDTO dto = new ProductRequestDTO("Produto", "Descricao", 10, 99.99);
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        when(productRepository.save(
                ArgumentMatchers.any(Product.class)))
                .thenReturn(savedProduct);

        Product result = productService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Test creating a product")
    void testUpdateProduct_Success() {
        ProductUpdateRequestDTO productUpdateDTO = new ProductUpdateRequestDTO("Produto Atualizado",
                "Descricao Atualizada", 49.99);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product1);

        Product result = productService
                .update(product1.getId(), productUpdateDTO);

        assertNotNull(result);
        assertEquals("Produto Atualizado", result.getName());
        assertEquals("Descricao Atualizada", result.getDescription());
    }

    @Test
    @DisplayName("Test updating a product not found")
    void testUpdateProduct_ProductNotFound() {
        ProductUpdateRequestDTO productUpdateDTO = new ProductUpdateRequestDTO("Produto Atualizado",
                "Descricao Atualizada", 49.99);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.update(1L, productUpdateDTO));
    }

    @Test
    @DisplayName("Test remove a produtct")
    void testRemoveProduct_Success() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product1))
                .thenReturn(Optional.empty());

        productService.delete(1L);

        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));
    }

    @Test
    @DisplayName("Test updating product quantity")
    void testUpdateQuantity_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product1);

        Product result = productService.updateQuantity(1L, 50);

        assertNotNull(result);
        assertEquals(50, result.getAvailableQuantity());
    }

    @Test
    @DisplayName("Test updating product quantity invalid")
    void testCreateProduct_InvalidQuantity() {
        ProductRequestDTO dto = new ProductRequestDTO("Produto", "Descricao", 0, 99.99);

        assertThrows(AvaliableQuantityProductException.class, () -> productService.create(dto));
    }

    @Test
    @DisplayName("Test validating order item - Success")
    void testValidateOrderItem_Success() {
        CreateOrderItemDTO item = new CreateOrderItemDTO(product1.getId(), 10, null);
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        InventoryResponseDTO response = productService.validateOrderItem(item);

        assertNotNull(response);
        assertEquals(InventoryStatus.SUCCESS, response.status());
        assertTrue(response.message().contains("validado com sucesso"));
        assertEquals(product1, response.data());
    }

    @Test
    @DisplayName("Test validating order item - Insufficient Stock")
    void testValidateOrderItem_InsufficientStock() {
        CreateOrderItemDTO item = new CreateOrderItemDTO(product1.getId(), 200, null);
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        InventoryResponseDTO response = productService.validateOrderItem(item);

        assertNotNull(response);
        assertEquals(InventoryStatus.ERROR, response.status());
        assertTrue(response.message().contains("Estoque insuficiente"));
        assertNull(response.data());
    }

    @Test
    @DisplayName("Test validating order item - Product Not Found")
    void testValidateOrderItem_ProductNotFound() {
        long invalidId = 999L;
        CreateOrderItemDTO item = new CreateOrderItemDTO(invalidId, 1, null);
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        InventoryResponseDTO response = productService.validateOrderItem(item);

        assertNotNull(response);
        assertEquals(InventoryStatus.ERROR, response.status());
        assertTrue(response.message().contains("não encontrado"));
        assertNull(response.data());
    }

    @Test
    @DisplayName("Test product is insufficient stock")
    void testIsInsufficientStock() {
        assertEquals(true, productService.isInsufficientStock(5, 3));
        assertEquals(false, productService.isInsufficientStock(2, 5));
        assertEquals(false, productService.isInsufficientStock(0, 5));
        assertEquals(true, productService.isInsufficientStock(5, 0));
    }

    @Test
    @DisplayName("Test product is available quantity invalid")
    void testIsAvailableQuantityInvalid() {
        assertEquals(true, productService.isAvailableQuantityInvalid(0));
        assertEquals(true, productService.isAvailableQuantityInvalid(-1));
        assertEquals(false, productService.isAvailableQuantityInvalid(1));
        assertEquals(false, productService.isAvailableQuantityInvalid(null));
    }

    @Test
    @DisplayName("Test creating success response - Method")
    void testCreateSuccessResponse() {
        String message = String.format("Produto ID %d validado com sucesso", product1.getId());
        Product data = product1;

        InventoryResponseDTO response = productService.createSuccessResponse(message, data);

        assertNotNull(response);
        assertEquals(InventoryStatus.SUCCESS, response.status());
        assertEquals(message, response.message());
        assertEquals(data, response.data());
    }

    @Test
    @DisplayName("Test creating error response - Method")
    void testCreateErrorResponse() {
        String errorMsg = String.format("Erro ao validar o pedido: %s. Tente novamente mais tarde.",
                "Falha inesperada");

        InventoryResponseDTO response = productService.createErrorResponse(errorMsg);

        assertNotNull(response);
        assertEquals(InventoryStatus.ERROR, response.status());
        assertEquals(errorMsg, response.message());
        assertNull(response.data());
    }
}
