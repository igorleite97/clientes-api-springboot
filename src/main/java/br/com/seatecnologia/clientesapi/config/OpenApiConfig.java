package br.com.seatecnologia.clientesapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configura o Swagger/OpenAPI com autenticação Basic Auth.
 *
 * Com isso, o botão "Authorize" aparece no Swagger UI —
 * o avaliador pode testar os endpoints direto pelo navegador
 * sem precisar de Postman ou curl.
 *
 * Usuários disponíveis para teste:
 *   admin / 123qwe!@# → acesso total (GET, POST, PUT, DELETE)
 *   user  / 123qwe123 → somente leitura (GET)
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Clientes API",
                version = "1.0.0",
                description = "API REST para gerenciamento de clientes — Desafio SEA Tecnologia"
        )
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {
    // Toda a configuração foi feita via anotações acima
}
