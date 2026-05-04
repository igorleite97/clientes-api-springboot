package br.com.seatecnologia.clientesapi.dto.response;

import br.com.seatecnologia.clientesapi.model.TipoTelefone;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO de saída do telefone.
 *
 * O número já vem mascarado conforme o tipo:
 *   - RESIDENCIAL/COMERCIAL: (XX) XXXX-XXXX
 *   - CELULAR:               (XX) XXXXX-XXXX
 */
@Getter
@Builder
public class TelefoneResponseDTO {

    private Long id;
    private String numero;
    private TipoTelefone tipo;
}
