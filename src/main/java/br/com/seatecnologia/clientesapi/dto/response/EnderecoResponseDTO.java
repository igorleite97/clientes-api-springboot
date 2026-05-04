package br.com.seatecnologia.clientesapi.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de saída do endereço.
 *
 * O CEP é exibido COM máscara (ex: 01310-100),
 * mas foi persistido no banco SEM máscara (ex: 01310100).
 */
@Getter
@Builder
public class EnderecoResponseDTO {

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
}
