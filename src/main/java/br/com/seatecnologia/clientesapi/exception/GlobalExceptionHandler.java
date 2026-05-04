package br.com.seatecnologia.clientesapi.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralizador de tratamento de exceções.
 *
 * Sem essa classe, qualquer exceção não tratada retornaria um JSON padrão
 * do Spring com stacktrace — péssimo para segurança e usabilidade.
 *
 * @RestControllerAdvice: intercepta exceções de todos os controllers
 * e retorna a resposta no formato ErroResponse, sempre consistente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bean Validation falhou: campo obrigatório ausente, tamanho errado, CPF inválido etc.
     * Coleta todos os erros de uma vez e retorna 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex) {

        // Monta um mapa campo → mensagem de erro (ex: "cpf" → "CPF inválido")
        Map<String, String> detalhes = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                detalhes.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        ErroResponse resposta = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Corrija os campos abaixo e tente novamente",
                detalhes
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    /**
     * Cliente não encontrado pelo ID informado → 404.
     */
    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleClienteNaoEncontrado(ClienteNaoEncontradoException ex) {

        ErroResponse resposta = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                "Não encontrado",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    /**
     * CPF já existe no banco → 409 Conflict.
     * Não é erro de validação, é conflito de regra de negócio.
     */
    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<ErroResponse> handleCpfDuplicado(CpfJaCadastradoException ex) {

        ErroResponse resposta = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Conflito",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    /**
     * CEP não encontrado na ViaCEP → 422 Unprocessable Entity.
     * O formato estava certo, mas o conteúdo não é válido.
     */
    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ErroResponse> handleCepInvalido(CepInvalidoException ex) {

        ErroResponse resposta = new ErroResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "CEP inválido",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta);
    }

    /**
     * Validação falhou na camada de entidade JPA (ex: telefone com dígitos errados).
     *
     * Isso acontece quando algum dado passa pelo DTO mas falha nas anotações
     * da entidade (@Pattern, @Size etc.) antes do Hibernate persistir.
     * Sem esse handler, o Spring retornaria 500 — aqui mapeamos para 400.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> detalhes = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            // O path vem como "metodo.campo" — pegamos só a última parte
            String campo = violation.getPropertyPath().toString();
            String campoSimples = campo.contains(".") ? campo.substring(campo.lastIndexOf('.') + 1) : campo;
            detalhes.put(campoSimples, violation.getMessage());
        });

        ErroResponse resposta = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Corrija os campos abaixo e tente novamente",
                detalhes
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    /**
     * JSON malformado na requisição (chaves faltando, vírgula extra etc.) → 400.
     *
     * Sem esse handler, o Spring exporia detalhes do parser interno
     * na resposta — o que é um problema de segurança.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {

        ErroResponse resposta = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                "O corpo da requisição está mal formatado. Verifique o JSON enviado"
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    /**
     * Violação de constraint no banco de dados → 409.
     *
     * Defesa em profundidade: mesmo que o service já cheque CPF duplicado,
     * uma race condition em requisições simultâneas poderia burlar essa checagem.
     * Aqui garantimos que o banco nunca retornaria 500 nesse cenário.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleIntegridadeBanco(DataIntegrityViolationException ex) {

        ErroResponse resposta = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Conflito de dados",
                "Já existe um registro com esses dados. Verifique campos únicos como CPF"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    /**
     * Qualquer erro inesperado → 500.
     * A mensagem é genérica para não vazar detalhes internos ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleErroGenerico(Exception ex) {

        ErroResponse resposta = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}
