# Delivery System - Projeto Diamante 02

Monorepo de microsservicos distribuidos para o sistema de delivery, desenvolvido com Spring Cloud, RabbitMQ, Spring AI e H2 Database.

## Integrantes do Grupo

- Joao Henrique Batista - RM564361
- Gutemberg Rocha - RM562267
- Gustavo Arthur Carvalho Sartori - RM561650

---

## Arquitetura de Microsservicos

O monorepo e composto por 4 subprojetos gerenciados por um settings.gradle raiz:

1. eureka-server (Porta 8761): Servidor central de Service Discovery Eureka.
2. payment-service (Portas 8081 e 8082): Microsservico de processamento de pagamentos com simulacao de falha de aproximadamente 50% e registro em log da porta da instancia.
3. order-service (Porta 8080): Servico de gestao de pedidos e cardapio, controle de estoque com lock pessimista, chamada de pagamento com balanceamento de carga e retry com backoff exponencial, publicacao assincrona de avaliacoes no RabbitMQ, assistente de IA com Spring AI e limitador de taxa (rate limiting) de 20 requisicoes por segundo.
4. review-service (Porta 8083): Servico consumidor de mensageria com mecanismo de buffer em memoria (ConcurrentHashMap), consolidacao agendada a cada 5 segundos e endpoint de ranking ordenado por media.

---

## Pre-requisitos

- Java 17 LTS instalado
- Docker e Docker Compose instalados
- Chave de API OpenAI configurada na variavel de ambiente OPENAI_API_KEY

---

## Passo a Passo para Execucao

### 1. Iniciar Infraestrutura RabbitMQ

Na raiz do projeto delivery, execute:

```bash
docker compose up -d
```

O container rabbitmq sera iniciado nas portas 5672 (AMQP) e 15672 (Management UI).

### 2. Configurar a Variavel de Ambiente da OpenAI

No Windows (PowerShell):

```powershell
$env:OPENAI_API_KEY="sk-proj-..."
```

No Linux/macOS (Bash/Zsh):

```bash
export OPENAI_API_KEY="sk-proj-..."
```

### 3. Compilacao do Monorepo

Execute a construcao completa de todos os modulos:

```bash
./gradlew build -x test
```

No Windows:

```powershell
.\gradlew.bat build -x test
```

### 4. Inicializacao dos Microsservicos

Inicie os servicos em terminais separados na seguinte sequencia:

#### Terminal 1 - Eureka Server (:8761)
```powershell
.\gradlew.bat :eureka-server:bootRun
```
Aguarde a inicializacao completa em http://localhost:8761.

#### Terminal 2 - Payment Service Instancia 1 (:8081)
```powershell
.\gradlew.bat :payment-service:bootRun
```

#### Terminal 3 - Payment Service Instancia 2 (:8082)
```powershell
.\gradlew.bat :payment-service:bootRun --args='--server.port=8082'
```

#### Terminal 4 - Order Service (:8080)
```powershell
.\gradlew.bat :order-service:bootRun
```

#### Terminal 5 - Review Service (:8083)
```powershell
.\gradlew.bat :review-service:bootRun
```

---

## Contrato de Endpoints e Portas

### Order Service (Porta :8080)

- GET /dishes: Retorna a lista de pratos disponiveis.
- GET /dishes/{id}: Retorna o prato especificado ou 404 se nao encontrado.
- POST /orders: Cria um novo pedido. Payload: {"dishId": 1, "quantity": 1}.
  - Retorna 201 Created em caso de sucesso.
  - Retorna 400 Bad Request se quantity < 1.
  - Retorna 404 Not Found se o prato nao existir.
  - Retorna 409 Conflict se o estoque for insuficiente.
  - Retorna 429 Too Many Requests se ultrapassar 20 requisicoes por segundo.
  - Retorna 502 Bad Gateway se o pagamento falhar apos todas as tentativas de retry (estoque preservado).
- GET /orders/{id}: Retorna o pedido ou 404.
- POST /reviews: Publica avaliacao para fila RabbitMQ. Payload: {"dishId": 1, "rating": 5, "comment": "Great"}.
  - Retorna 202 Accepted imediatamente sem persistir diretamente no banco.
- POST /assistant: Consulta ao atendente de IA. Payload: {"question": "Quais sao as opcoes do cardapio?"}.
  - Retorna 200 OK com {"answer": "..."}.

### Payment Service (Portas :8081 / :8082)

- POST /payments: Processa cobranca simulada. Payload: {"amount": 29.90}.
  - Retorna 200 OK com {"status": "APPROVED", "instance": 8081} ou 500 Internal Server Error.

### Review Service (Porta :8083)

- GET /reviews/ranking: Retorna a lista consolidada de pratos ordenada por media decrescente.
  - Formato: [{"dishId": 1, "dishName": "House Burger", "average": 4.6, "count": 128}]

---

## Execucao dos Testes Automatizados

Para executar os testes unitarios e de integracao do monorepo:

```powershell
.\gradlew.bat test
```
