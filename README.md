# Clientes API

API REST desenvolvida com Spring Boot para gerenciamento de clientes, com validações robustas, integração com API externa (ViaCEP) e aplicação de boas práticas de arquitetura e segurança.

---

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate 6)
- H2 Database (ambiente de desenvolvimento)
- Spring Security (Basic Auth)
- OpenFeign (integração com ViaCEP)
- Swagger / OpenAPI (springdoc)
- Maven

---

## Arquitetura

A aplicação segue uma arquitetura em camadas:

Controller → Service → Repository → Database

### Principais decisões técnicas

- Uso de DTOs para desacoplamento entre API e modelo de domínio
- Validação de CPF com algoritmo matemático (não apenas regex)
- Tratamento global de exceções com `@RestControllerAdvice`
- Separação de responsabilidades por camada
- Controle de transações na camada de serviço (`@Transactional`)
- Relacionamentos com `FetchType.LAZY` para evitar sobrecarga desnecessária
- Uso de `orphanRemoval = true` para gerenciamento automático de entidades filhas
- Integração externa com ViaCEP via OpenFeign

---

## Segurança

- Autenticação via Basic Auth
- Controle de acesso baseado em roles:

| Role  | Permissão        |
|-------|------------------|
| USER  | Leitura          |
| ADMIN | Escrita (CRUD)   |

---

## Como executar o projeto

### Pré-requisitos

- Java 17 ou superior
- Maven (ou utilizar o Maven Wrapper incluído)

### Passo a passo

```bash
git clone https://github.com/SEU-USUARIO/clientesapi.git
cd clientesapi

# Linux / Mac
./mvnw spring-boot:run

# Windows
./mvnw.cmd spring-boot:run
```
A aplicação estará disponível em:
```
http://localhost:8080
```
---
## Banco de Dados (H2)
Console disponível em:
```
http://localhost:8080/h2-console
```
### Configuração
- JDBC URL: `jdbc:h2:mem:clientesdb`
- Usuário: `sa`
- Senha: (em branco)

Observação: o banco é em memória e os dados são resetados a cada reinicialização da aplicação.

---
## Documentação da API
A documentação interativa está disponível via Swagger:
```
http://localhost:8080/swagger-ui.html
```
## Testes 
A aplicação contém um teste de inicialização (Smoke Test):

- Verifica se o contexto Spring é carregado corretamente
- Garante integridade básica da configuração da aplicação

---
## Dados de Demonstração

Ao iniciar a aplicação, são carregados automaticamente 3 clientes para facilitar testes.

Isso permite validar imediatamente:

- Listagem de clientes
- Busca por ID
- Atualização
- Remoção

Sem necessidade de cadastro manual prévio.

---
## Integração com ViaCEP
Durante o cadastro de um cliente:

- O CEP informado é utilizado para buscar dados automaticamente na API ViaCEP
- Os dados retornados podem ser sobrescritos manualmente

---
## Decisões arquiteturais relevantes

- OSIV desabilitado `(spring.jpa.open-in-view=false)` para controle adequado do ciclo de vida das transações
- Uso de DTOs para evitar exposição direta de entidades JPA
- Tratamento de exceções centralizado
- Validação consistente em múltiplas camadas (API + banco)

---
## Melhorias futuras
- Autenticação com JWT
- Banco de dados PostgreSQL com Flyway (versionamento de schema)
- Circuit Breaker para integração externa `(Resilience4j)`
- Ampliação da cobertura de testes (unitários e integração)
- Externalização de configurações sensíveis

---
### Autor
Igor Leite de Andrade  
Security-Oriented Software Engineer