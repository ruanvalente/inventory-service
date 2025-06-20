package com.inventoryservice.ms.inventory_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryservice.ms.inventory_service.controllers.ProductController;
import com.inventoryservice.ms.inventory_service.entities.Product;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.ProductUpdateRequestDTO;
import com.inventoryservice.ms.inventory_service.entities.dto.request.UpdateQuantityDTO;
import com.inventoryservice.ms.inventory_service.exceptions.ProductNotFoundException;
import com.inventoryservice.ms.inventory_service.services.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/products";

    private Product product1;
    private Product product2;

    private Product buildProduct(Long id, String name, String description, int quantity, double price) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.setAvailableQuantity(quantity);
        p.setPrice(price);
        return p;
    }

    @BeforeEach
    void setUp() {
        product1 = buildProduct(1L, "Produto 1", "Descrição do Produto 1", 100, 19.99);
        product2 = buildProduct(2L, "Produto 2", "Descrição do Produto 2", 50, 29.99);
    }

    @Test
    void testListAllProducts() throws Exception {
        List<Product> products = List.of(product1, product2);
        Page<Product> page = new PageImpl<>(products);

        Mockito.when(productService.listAll(any())).thenReturn(page);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(2))
                .andExpect(jsonPath("$.content[0].id")
                        .value(1L))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Produto 1"))
                .andExpect(jsonPath("$.content[1].id")
                        .value(2L))
                .andExpect(jsonPath("$.content[1].name")
                        .value("Produto 2"))
                .andExpect(jsonPath("$.totalElements")
                        .value(2));
    }

    @Test
    void testGetProductById() throws Exception {
        Mockito.when(productService.findById(1L))
                .thenReturn(product1);

        mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductRequestDTO dto = new ProductRequestDTO("Produto", "Descricao", 10, 99.99);

        Mockito.when(productService.create(any(ProductRequestDTO.class)))
                .thenReturn(product1);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Long id = 1L;
        ProductUpdateRequestDTO updateDTO = new ProductUpdateRequestDTO("Produto Atualizado", "Nova descrição", 29.99);
        Product updatedProduct = new Product();
        updatedProduct.setId(id);
        updatedProduct.setName("Produto Atualizado");
        updatedProduct.setDescription("Nova descrição");
        updatedProduct.setAvailableQuantity(50);
        updatedProduct.setPrice(29.99);

        Mockito.when(productService.update(Mockito.eq(id), Mockito.any(ProductUpdateRequestDTO.class)))
                .thenReturn(updatedProduct);

        mockMvc.perform(put(BASE_URL + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Produto Atualizado"))
                .andExpect(jsonPath("$.description").value("Nova descrição"))
                .andExpect(jsonPath("$.availableQuantity").value(50))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    void testUpdateProductQuantity() throws Exception {
        Long id = 1L;
        int newQuantity = 99;
        UpdateQuantityDTO dto = new UpdateQuantityDTO(newQuantity);

        Product updatedProduct = new Product();
        updatedProduct.setId(id);
        updatedProduct.setAvailableQuantity(newQuantity);

        Mockito.when(productService.updateQuantity(Mockito.eq(id), Mockito.eq(newQuantity)))
                .thenReturn(updatedProduct);

        mockMvc.perform(patch(BASE_URL + "/" + id + "/quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.availableQuantity").value(newQuantity));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Long productId = product1.getId();
        Mockito.doNothing().when(productService).delete(productId);

        mockMvc.perform(delete(BASE_URL + "/" + productId))
                .andExpect(status().isNoContent());

        Mockito.verify(productService).delete(productId);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        Long nonExistentId = 99L;
        Mockito.when(productService.findById(nonExistentId)).thenThrow(new ProductNotFoundException(nonExistentId));

        mockMvc.perform(get(BASE_URL + "/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(
                        content().string(
                                org.hamcrest.Matchers.containsString("Product with " + nonExistentId + " not found")));
    }

    @Test
    void testCreateProduct_InvalidRequest() throws Exception {
        ProductRequestDTO dto = new ProductRequestDTO("", "", -1, -10.0);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListAllProducts_Empty() throws Exception {
        Page<Product> emptyPage = new PageImpl<>(List.of());
        Mockito.when(productService.listAll(any())).thenReturn(emptyPage);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isNoContent());
    }

}
