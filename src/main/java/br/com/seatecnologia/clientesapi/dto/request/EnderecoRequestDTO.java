package br.com.seatecnologia.clientesapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para o endereço do cliente.
 *
 * Apenas o CEP é obrigatório na entrada, os demais campos
 * são preenchidos automaticamente via ViaCEP no service.
 *
 * O usuário PODE sobrescrever qualquer campo que veio da ViaCEP:
 * se ele mandar logradouro preenchido, usamos o dele; caso contrário,
 * usamos o que a ViaCEP retornou.
 *
 * CEP aceita com ou sem máscara:
 *   - "01310-100" ok
 *   - "01310100"  ok
 * O service remove a máscara antes de persistir.
 */
@Getter
@Setter
public class EnderecoRequestDTO {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(
            regexp = "\\d{5}-?\\d{3}",
            message = "CEP inválido. Informe no formato XXXXX-XXX ou XXXXXXXX"
    )
    private String cep;

    // Opcionais — se não vier preenchido, o service usa o retorno da ViaCEP
    private String logradouro;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
}
