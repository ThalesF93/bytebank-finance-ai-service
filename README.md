# ByteBank Finance AI Service
 
Microsserviço de inteligência financeira do ecossistema ByteBank. Recebe mensagens de voz ou texto via WhatsApp, interpreta a intenção com GPT-4o-mini e registra ou consulta transações financeiras em linguagem natural.
 
## Visão Geral
 
O usuário envia um áudio ou mensagem de texto pelo WhatsApp dizendo algo como *"gastei 50 reais no mercado"* ou *"quanto gastei esse mês?"*. O serviço transcreve, interpreta e age — sem nenhuma interface além do próprio WhatsApp.
 
Além disso, o serviço consome automaticamente todos os eventos de transação bancária publicados pelo `transaction-service` via Kafka, mantendo o histórico financeiro completo e atualizado.
 
```
Áudio/Texto (WhatsApp)
        ↓
   API Gateway
        ↓
Finance AI Service
   ├── Whisper (transcrição)
   ├── GPT-4o-mini (intenção)
   │    ├── @Tool → persiste transação no PostgreSQL
   │    └── RAG → busca no pgvector → responde com dados reais
   └── Resposta em texto → WhatsApp
 
Kafka (transaction-service)
        ↓
Finance AI Service
   ├── Persiste operação no PostgreSQL
   └── Gera embedding → salva no pgvector
```
 
## Funcionalidades
 
**Registro por voz ou texto**
O usuário descreve uma transação em linguagem natural. O GPT interpreta valor, categoria e descrição, e persiste automaticamente via function calling (`@Tool`).
 
**Consulta por linguagem natural com RAG**
O usuário pergunta sobre seus gastos. O serviço gera um embedding da pergunta, busca as transações semanticamente mais relevantes no pgvector e alimenta o GPT com esses dados para uma resposta precisa e contextualizada.
 
**Consumo de eventos bancários via Kafka**
Toda transação processada pelo `transaction-service` (depósito, saque, transferência) publica um evento no tópico `transaction.created`. Este serviço consome esses eventos, persiste a operação e gera o embedding correspondente.
 
**Idempotência com Redis**
Eventos Kafka duplicados são detectados e ignorados via chave de idempotência com TTL de 24 horas.
 
## Stack
 
| Camada | Tecnologia |
|--------|------------|
| Framework | Spring Boot 4.x, Spring AI 2.x |
| IA | OpenAI GPT-4o-mini, Whisper, text-embedding-ada-002 |
| Busca vetorial | pgvector (extensão PostgreSQL) |
| Mensageria | Apache Kafka (KRaft) |
| Cache / Idempotência | Redis |
| Service Discovery | Netflix Eureka |
| Comunicação síncrona | OpenFeign |
| Banco de dados | PostgreSQL 16 |
| Observabilidade | Prometheus, Zipkin |
| Containerização | Docker, Docker Compose |
 
## Arquitetura
 
O projeto segue Clean Architecture com separação em três camadas:
 
```
src/main/java/br/com/financeaiservice/
├── application/
│   ├── input/          # DTOs de entrada
│   ├── output/         # DTOs de saída
│   ├── service/        # Serviços de aplicação (LLM, TTS, Transcrição)
│   └── usecase/        # Casos de uso (processar WhatsApp, persistir operação)
├── domain/
│   ├── entity/         # Entidades de domínio
│   ├── enums/          # Categorias financeiras
│   └── repository/     # Contratos de repositório
└── infrastructure/
    ├── adapters/        # Controllers HTTP
    ├── client/          # Feign clients
    ├── config/          # Configurações (Kafka, Redis, ChatClient, pgvector)
    ├── context/         # UserContext (RequestScope)
    ├── messaging/       # Consumer Kafka + eventos
    └── exception/       # Exceções customizadas
```
 
## Como Executar
 
### Pré-requisitos
 
- Docker e Docker Compose instalados
- Rede Docker `bytebank-net` criada (`docker network create bytebank-net`)
- Serviços de infraestrutura rodando na rede: Kafka, Redis, Eureka, RabbitMQ
- Chave de API da OpenAI
### Variáveis de Ambiente
 
Crie um arquivo `.env` na raiz do projeto:
 
```env
OPENAI_API_KEY=sk-sua-chave-aqui
```
 
### Subindo o serviço
 
```bash
docker compose -p finance-ai-service up -d --build
```
 
### Habilitando o pgvector
 
Na primeira execução, habilite a extensão no banco:
 
```bash
docker exec -it finance-ai-db psql -U postgres -d finance_ai_db -c "CREATE EXTENSION IF NOT EXISTS vector;"
```
 
### Endpoints
 
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/operations/whatsapp` | Recebe áudio ou texto do WhatsApp |
| POST | `/api/v1/operations/create` | Persiste operação diretamente |
 
O endpoint `/whatsapp` aceita `multipart/form-data` com os campos:
- `audio` (opcional): arquivo de áudio MP3
- `text` (opcional): mensagem em texto
O header `X-User-Id` é injetado pelo API Gateway via `ResolveUserGatewayFilterFactory`.
 
## Categorias Disponíveis
 
`PERSONAL`, `PHARMACY`, `SCHOOL`, `SHOPPING`, `GAS`, `GROCERIES`, `WARDROBE`, `PET`, `HOUSE`, `MARKET`, `CHILDREN`, `BANK_OPERATION`
 
## Integração WhatsApp
 
O fluxo de integração utiliza WAHA (WhatsApp HTTP API) + n8n:
 
1. WAHA recebe a mensagem e dispara um webhook para o n8n
2. n8n filtra por número autorizado e tipo de chat (privado)
3. n8n encaminha para o endpoint `/whatsapp` com o número de telefone no header
4. O API Gateway resolve o número para o `customerId` correspondente
## Repositório
 
Parte do ecossistema [ByteBank](https://github.com/ThalesF93/Bytebank-hub)
