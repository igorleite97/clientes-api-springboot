package br.com.seatecnologia.clientesapi.exception;

/**
 * Lançada quando o CEP informado não é encontrado na ViaCEP
 * ou quando a API externa retorna erro.
 *
 * O GlobalExceptionHandler retorna HTTP 422 (Unprocessable Entity)
 * o dado veio no formato certo, mas não é um CEP válido/existente.
 */
public class CepInvalidoException extends RuntimeException {

    public CepInvalidoException(String cep) {
        super("CEP '" + cep + "' não encontrado. Verifique se o CEP está correto");
    }
}
