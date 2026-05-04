package br.com.seatecnologia.clientesapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Modelo padronizado de resposta de erro.
 *
 * Toda exceção tratada pelo GlobalExceptionHandler retorna esse formato
 * o que facilita muito o tratamento no front-end — sempre o mesmo contrato.
 *
 * O campo 'detalhes' só aparece no JSON quando preenchido (erros de validação),
 * graças ao @JsonInclude(NON_NULL).
 *
 * Exemplo de resposta para erro de validação (400):
 * {
 *   "timestamp": "2024-10-20T14:32:00",
 *   "status": 400,
 *   "erro": "Dados inválidos",
 *   "mensagem": "Verifique os campos abaixo",
 *   "detalhes": {
 *     "nome": "Nome deve ter entre 3 e 100 caracteres",
 *     "cpf": "CPF inválido"
 *   }
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        Map<String, String> detalhes
) {

    // Construtor de conveniência para erros simples (sem detalhes de campo)
    public ErroResponse(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem, null);
    }

    // Construtor para erros de validação (com detalhes por campo)
    public ErroResponse(int status, String erro, String mensagem, Map<String, String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, detalhes);
    }
}
