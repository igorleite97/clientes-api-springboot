package br.com.seatecnologia.clientesapi.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * DTO de saída com os dados do cliente.
 *
 * Aqui os dados sensíveis já vêm mascarados:
 *   - CPF:  XXX.XXX.XXX-XX
 *   - CEP:  XXXXX-XXX (dentro de EnderecoResponseDTO)
 *   - Tel:  (XX) XXXX-XXXX ou (XX) XXXXX-XXXX (dentro de TelefoneResponseDTO)
 *
 * Nunca retornamos a entidade Cliente diretamente — isso evita
 * serializar campos internos do JPA e referências circulares.
 */
@Getter
@Builder
public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String cpf;
    private EnderecoResponseDTO endereco;
    private List<TelefoneResponseDTO> telefones;
    private List<EmailResponseDTO> emails;
}
