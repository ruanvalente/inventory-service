package com.inventoryservice.ms.inventory_service.entities.enums;

public enum InventoryStatus {
  SUCCESS("Operação concluída com sucesso"),
  ERROR("Erro ao processar a operação");

  private final String mensagem;

  InventoryStatus(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getMensagem() {
    return mensagem;
  }
}
