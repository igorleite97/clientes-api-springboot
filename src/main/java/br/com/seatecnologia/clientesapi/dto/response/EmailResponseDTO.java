package br.com.seatecnologia.clientesapi.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de saída do e-mail.
 */
@Getter
@Builder
public class EmailResponseDTO {

    private Long id;
    private String endereco;
}
