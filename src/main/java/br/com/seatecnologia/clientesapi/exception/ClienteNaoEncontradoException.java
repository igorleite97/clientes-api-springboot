package br.com.seatecnologia.clientesapi.exception;

/**
 * Lançada quando tentamos buscar, atualizar ou deletar
 * um cliente que não existe no banco.
 *
 * O GlobalExceptionHandler captura essa exceção e retorna HTTP 404.
 */
public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(Long id) {
        super("Cliente com ID " + id + " não encontrado");
    }
}
