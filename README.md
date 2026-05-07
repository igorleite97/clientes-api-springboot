# ClientesAPI

API REST desenvolvida com **Java 17 + Spring Boot 3** para gerenciamento de clientes.
Implementa validações robustas, integração com ViaCEP via OpenFeign, autenticação com Spring Security e boas práticas de arquitetura e segurança.

> **Desafio Técnico — SEA Tecnologia | 2ª Etapa**

---

## Como executar

### Pré-requisitos

- **Java 17+** — [Eclipse Temurin 17](https://adoptium.net)
- Git
- Porta **8080** disponível
- Maven **não é necessário** — o projeto inclui o Maven Wrapper (`.mvnw`)

### 3 comandos para rodar

```bash
# 1. Clonar o repositório
git clone https://github.com/igorleite97/clientes-api-springboot.git
cd clientes-api-springboot

# 2. Executar os testes (Smoke Test — obrigatório antes de subir)
./mvnw test          # Linux / Mac
.\mvnw.cmd test      # Windows

# Resultado esperado:
# [INFO] Tests run: 2, Failures: 0, Errors: 0
# [INFO] BUILD SUCCESS

# 3. Subir a aplicação
./mvnw spring-boot:run          # Linux / Mac
.\mvnw.cmd spring-boot:run      # Windows

# Aguardar:
# Started ClientesApiApplication in X.XXX seconds
```

---

## URLs de acesso

| Recurso | URL | Obs. |
|---------|-----|------|
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | Público |
| **API Base** | http://localhost:8080/api/clientes | Requer auth |
| **H2 Console** | http://localhost:8080/h2-console | Somente ADMIN |
| **OpenAPI Docs** | http://localhost:8080/api-docs | Público |

---

## Credenciais

| Usuário | Senha | Role | Permissões |
|---------|-------|------|------------|
| `admin` | `123qwe!@#` | ADMIN | GET · POST · PUT · DELETE · H2 Console |
| `user` | `123qwe123` | USER | Somente GET `/api/clientes/**` |

> **H2 Console:** JDBC URL: `jdbc:h2:mem:clientesdb` · Username: `sa` · Password: *(em branco)*

---

## Tecnologias

| Tecnologia | Versão | Papel |
|------------|--------|-------|
| Java | 17 | Linguagem — LTS, Records, Text Blocks |
| Spring Boot | 3.3.5 | Framework principal — autoconfiguração, Tomcat embarcado |
| Spring Data JPA | gerenciado | ORM — Spring Data + Hibernate 6 |
| Hibernate | 6.5.x | Dialeto detectado automaticamente via `DialectDetector` |
| H2 Database | runtime | Banco em memória — zero setup para avaliação |
| Spring Security | 6.x | Autenticação Basic Auth + RBAC por roles |
| OpenFeign | 2023.0.3 | Cliente HTTP declarativo — integração ViaCEP |
| Springdoc OpenAPI | 2.6.0 | Swagger UI + documentação de endpoints |
| Lombok | 1.18.32 | Redução de boilerplate |
| Maven Wrapper | incluso | Build sem instalação local |

---

## Arquitetura

```
HTTP Request
    │
    ▼
Spring Security (BCrypt · STATELESS · CSRF off por arquitetura)
    │
    ▼
Controller (recebe HTTP · delega · retorna status)
    │
    ▼
Service (@Transactional · regras de negócio · máscaras · ViaCEP)
    │
    ▼
Repository (Spring Data JPA · PreparedStatement · zero SQL manual)
    │
    ▼
H2 (dados sem máscara · CPF/CEP/Telefone em dígitos puros)
```

A aplicação segue **Layered Architecture** — cada camada conhece apenas a imediatamente abaixo. Arquitetura escolhida por ser adequada à complexidade do problema, mantendo clareza e baixo acoplamento sem overhead desnecessário.

---

## Decisões de Engenharia

### `spring.jpa.open-in-view=false`
Definido **explicitamente** (o padrão do Spring Boot é `true`). Com OSIV ativo, as conexões do HikariPool ficam ocupadas durante toda a serialização da resposta HTTP. Com `false`, a conexão é liberada ao fim do `@Transactional` no Service — pool mais eficiente e arquitetura que se impõe: banco só dentro da camada transacional.


### CSRF desabilitado por arquitetura
CSRF explora sessões de browser. Esta API usa `SessionCreationPolicy.STATELESS` — sem sessão, sem cookie de autenticação. O header `Authorization` é enviado **explicitamente** em cada request pelo cliente. O vetor de ataque do CSRF não existe aqui. Manter o CSRF causaria erros no Swagger e Postman sem adicionar proteção real.

### Mascaramento na camada certa
**Banco:** CPF, CEP e telefone salvos **sem máscara** (dígitos puros).  
**Resposta JSON:** exibidos **com máscara** via `MascaraUtil`.  
Isso permite queries consistentes no banco (`WHERE cpf = '52998224725'`) sem depender do formato do input.

### Race Condition no CPF
Dupla proteção: `existsByCpf()` no Service (primeira defesa) + constraint `UNIQUE` no banco (segunda defesa). Se duas threads passarem pelo check simultaneamente, o banco rejeita a segunda inserção com `DataIntegrityViolationException` → `GlobalExceptionHandler` → HTTP 409.

---

## Segurança

| Endpoint | Método | Role | Status se negado |
|----------|--------|------|-----------------|
| `/api/clientes/**` | GET | ADMIN ou USER | 403 |
| `/api/clientes` | POST | ADMIN | 403 |
| `/api/clientes/{id}` | PUT | ADMIN | 403 |
| `/api/clientes/{id}` | DELETE | ADMIN | 403 |
| `/h2-console/**` | GET | ADMIN | 403 |
| `/swagger-ui/**` | GET | Livre (permitAll) | — |
| `/api-docs/**` | GET | Livre (permitAll) | — |
| Qualquer rota | — | Sem credenciais | 401 |

**Três medidas de segurança implementadas:**
1. `@NomeSeguro` — regex Unicode `^[\p{L}\p{N} ]+$` bloqueia XSS antes do dado chegar ao banco
2. `GlobalExceptionHandler` — nunca expõe stacktrace ao usuário (HTTP 400/404/409/422/500 com mensagem amigável)
3. H2 Console protegido por `hasRole('ADMIN')` + `web-allow-others=false`

---

## Validações

| Campo | Regra | Tecnologia |
|-------|-------|------------|
| Nome | 3–100 chars · sem caracteres especiais | `@Size` + `@NomeSeguro` (custom) |
| CPF | Algoritmo matemático dos 2 dígitos verificadores | `@CPF` (custom `ConstraintValidator`) |
| CEP | Obrigatório · validado pela ViaCEP | `@NotBlank` + `CepInvalidoException` |
| Telefone | Pelo menos 1 · tipo obrigatório (CELULAR/RESIDENCIAL/COMERCIAL) | `@NotEmpty` + `@NotNull` |
| Email | Pelo menos 1 · formato válido | `@NotEmpty` + `@Email` |

---

## Integração ViaCEP

Implementada com **OpenFeign** (cliente HTTP declarativo). Fluxo:
1. Usuário envia o CEP no request
2. `MascaraUtil.apenasDigitos()` limpa a máscara — aceita CEP com ou sem hífen
3. Se o CEP possuir formato inválido (diferente de 8 dígitos) → validação local → HTTP 400 *(fail-fast antes da chamada externa)*
4. `ViaCepClient.buscarPorCep()` consulta `https://viacep.com.br/ws/{cep}/json/`
5. Se ViaCEP retornar `{"erro":"true"}` → `CepInvalidoException` → HTTP 422
6. Se ViaCEP cair (timeout/rede) → fallback: usa os dados enviados pelo usuário
7. Usuário pode sobrescrever qualquer campo retornado pela ViaCEP (`StringUtils.hasText()`)

---

## Dados de Demonstração

Ao iniciar, o `DataInitializer` carrega **3 clientes reais** automaticamente:

| Cliente | CPF | Cidade | Telefones |
|---------|-----|--------|-----------|
| João Carlos Pereira | 529.982.247-25 | São Paulo/SP | CELULAR + COMERCIAL |
| Ana Silva Santos | 275.484.389-23 | Rio de Janeiro/RJ | CELULAR + RESIDENCIAL |
| Carlos Eduardo Lima | 111.444.777-35 | Belo Horizonte/MG | COMERCIAL |

Implementado com `@Transactional` (os 3 saves são atômicos) e idempotente (não reinsere se os dados já existem).

---

## Testes

```bash
./mvnw test          # Linux / Mac
.\mvnw.cmd test      # Windows

# [INFO] Tests run: 2, Failures: 0, Errors: 0
# [INFO] BUILD SUCCESS
```

**Smoke Test** (`ClientesApiApplicationTests`): verifica que o `ApplicationContext` Spring sobe corretamente — todos os beans instanciados, configurações íntegras, `DataInitializer` executado. É a primeira etapa de qualquer pipeline de CI/CD.

> Atualmente a suíte contém testes de inicialização (smoke test). Testes unitários das regras de negócio (`CPFValidator`, `MascaraUtil`, `ClienteService`) e testes de integração estão mapeados como P4 no roadmap.

---

## Roadmap de Evolução

| Prioridade | Melhoria | Problema que resolve |
|-----------|----------|---------------------|
| P1 | PostgreSQL + Flyway | H2 perde dados no restart; Flyway versiona o schema |
| P2 | JWT + Refresh Token | Basic Auth envia credenciais em toda request; JWT limita ao login |
| P3 | Resilience4j + Cache Caffeine | ViaCEP cai sem Circuit Breaker = cascade failure; cache evita calls repetidos |
| P4 | Logs estruturados + Micrometer + Actuator | Sem observabilidade, produção é caixa preta |
| P4 | Testes unitários (JUnit 5 + Mockito) + integração | Cobertura de `CPFValidator`, `MascaraUtil`, `ClienteService` |
| P5 | Docker + CI/CD | Infraestrutura depende de aplicação estável |

---

## Estrutura do Projeto

```
src/main/java/br/com/seatecnologia/clientesapi/
├── client/          # ViaCepClient (OpenFeign)
├── config/          # SecurityConfig, OpenApiConfig
├── controller/      # ClienteController
├── dto/             # Request/Response DTOs
├── exception/       # GlobalExceptionHandler, exceções de domínio
├── model/           # Cliente, Telefone, Email, Endereco, TipoTelefone
├── repository/      # ClienteRepository (Spring Data JPA)
├── service/         # ClienteService (@Transactional)
├── util/            # MascaraUtil (CPF, CEP, Telefone)
└── validation/      # @CPF, @NomeSeguro e seus validators
```

---

## Autor

**Igor Leite de Andrade**  
Backend Developer · Offensive Security Student (eJPT)  
[github.com/igorleite97](https://github.com/igorleite97)
