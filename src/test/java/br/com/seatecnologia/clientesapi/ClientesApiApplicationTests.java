package br.com.seatecnologia.clientesapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste de integração básico que verifica se o contexto do Spring
 * sobe sem erros — beans configurados, injeções resolvidas,
 * banco inicializado, security chain ativa.
 *
 * Se qualquer configuração estiver quebrada, esse teste falha
 * antes mesmo de qualquer teste funcional rodar.
 */
@SpringBootTest
class ClientesApiApplicationTests {

    @Test
    void contextLoads() {
        // Se chegou aqui, todos os beans foram criados com sucesso.
        // Qualquer @Component, @Service, @Repository ou @Configuration
        // mal configurado quebraria antes de chegar nessa linha.
    }
}
