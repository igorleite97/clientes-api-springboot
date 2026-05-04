package br.com.seatecnologia.clientesapi.exception;

/**
 * Lançada quando tentamos cadastrar um CPF que já existe no banco.
 *
 * O GlobalExceptionHandler retorna HTTP 409 (Conflict) para esse caso —
 * faz mais sentido semântico que 400, pois os dados são válidos,
 * mas conflitam com um registro existente.
 */
public class CpfJaCadastradoException extends RuntimeException {

    public CpfJaCadastradoException(String cpf) {
        super("CPF " + cpf + " já está cadastrado no sistema");
    }
}
