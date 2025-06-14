# 🏪 inventory-service (Spring Boot)

Este microserviço é responsável pelo gerenciamento de **produtos** e **estoque**, sendo a principal referência de disponibilidade e precificação no sistema. Ele fornece dados atualizados de produtos e responde à validação de estoque ao receber solicitações via **RabbitMQ** oriundas do microserviço de pedidos (`order-service`).

---

## 🧱 Architecture

- Arquitetura baseada em **microserviços**
- Comunicação entre serviços via **RabbitMQ (mensageria)**
- Separação clara de camadas: Controller, Service, Repository
- Banco de dados relacional **PostgreSQL**
- Integração com Swagger para documentação
- Containerizado com **Docker + Docker Compose**
- Suporte a mensageria assíncrona para eventos como **OrderCreated**

---

## 📌 Technologies Used

| Layer            | Technology              |
| ---------------- | ----------------------- |
| Language         | Java 17                 |
| Framework        | Spring Boot 3.x         |
| Database         | PostgreSQL              |
| Messaging Queue  | RabbitMQ                |
| API Docs         | SpringDoc Swagger       |
| Containerization | Docker + Docker Compose |

---

## 📘 Entities and Relationships

### 📦 Product

| Field             | Type    | Required | Description                      |
| ----------------- | ------- | -------- | -------------------------------- |
| id                | integer | Sim      | Identificador único do produto   |
| name              | string  | Sim      | Nome do produto                  |
| description       | string  | Não      | Descrição detalhada do produto   |
| availableQuantity | integer | Sim      | Quantidade disponível em estoque |
| price             | decimal | Sim      | Preço unitário atual do produto  |

🔁 **Relationship:**

- Cada `Product` pode ser referenciado por múltiplos `OrderItem` no `order-service`.

---

## 🔁 Integração com o order-service

- Ao criar um pedido, o `order-service` envia uma mensagem para a fila `inventory-queue`.
- O `inventory-service` consome a mensagem e valida se há quantidade disponível no estoque.
- Com base nisso, ele aprova ou rejeita o pedido, podendo responder por evento ou callback REST (conforme versão futura).

---

## 🚀 Endpoints (Swagger)

- Documentação disponível em: `http://localhost:8081/swagger-ui.html`

### Exemplo de Endpoints:

- [x] `POST /products` – Cadastrar novo produto
- [x] `GET /products/:id` – Consultar produto por ID
- [x] `PUT /products/:id` – Atualizar dados do produto
- [x] `GET /products` – Listar todos os produtos
- `PATCH /products/:id/quantity` – Atualizar quantidade em estoque

### Exemplo de Collection ⚠️ (Em breve...)

---

## 📩 Mensageria - RabbitMQ

- 📥 Fila monitorada: `inventory-queue`
- Escutador configurado com `@RabbitListener`
- Espera mensagens no formato JSON representando pedidos criados
- Após validação, o produto é atualizado ou uma mensagem de erro pode ser gerada (em versões futuras)

---

## 🧪 Testing

- Estrutura preparada para testes com **JUnit** e **Mockito**
- Separação de testes unitários e de integração
- Simulações de consumo de mensagens com testes de mensageria
- Possibilidade de uso de Testcontainers para testes com PostgreSQL e RabbitMQ

---

## 🐳 Docker

### Run with Docker Compose:

```bash
docker-compose up --build
```

📂 Project Structure

```bash
inventory-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/inventory/
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── exceptions/
│   │   │   ├── repositories/
│   │   │   ├── service/
│   │   │   └── messaging/
│   ├── resources/
│   │   └── application.yml
├── Dockerfile
├── docker-compose.yml
├── data.sql
└── pom.xml
```
