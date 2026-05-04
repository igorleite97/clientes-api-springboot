package br.com.seatecnologia.clientesapi.exception;

/**
 * Lançada quando o CEP informado tem um formato inválido,
 * ou seja, não possui exatamente 8 dígitos numéricos após remover a máscara.
 *
 * Exemplos que disparam essa exceção:
 *   - "7151618"   → 7 dígitos (falta um)
 *   - "abc"       → sem dígitos
 *   - ""          → vazio
 *
 * Diferença em relação à CepInvalidoException:
 *   - CepFormatoInvalidoException → formato errado                  → HTTP 400
 *   - CepInvalidoException        → formato certo, CEP inexistente  → HTTP 422
 *
 * Essa distinção é importante para o cliente da API entender
 * se ele digitou errado (400) ou o CEP simplesmente não existe (422).
 */
public class CepFormatoInvalidoException extends RuntimeException {

    public CepFormatoInvalidoException(String cep) {
        super("CEP '" + cep + "' tem formato inválido. Informe exatamente 8 dígitos numéricos (ex: 01310-100 ou 01310100)");
    }
}
