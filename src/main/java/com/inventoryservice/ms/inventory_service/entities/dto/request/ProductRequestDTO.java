package com.inventoryservice.ms.inventory_service.entities.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDTO(
        @NotBlank(message = "O nome do produto é obrigatório") String name,
        @NotBlank(message = "A descrição do produto é obrigatória") String description,
        @NotNull(message = "A quantidade do produto deve ser informada com valor maior que 0") @Min(0) Integer availableQuantity,
        @NotNull(message = "O preço do produto deve ser maior que zero") @Min(0) Double price
) {

}
