package br.com.seatecnologia.clientesapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para e-mail do cliente.
 *
 * A anotação @Email do Jakarta Validation valida o formato RFC —
 * inclui casos como domínios inválidos e ausência de "@".
 */
@Getter
@Setter
public class EmailRequestDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String endereco;
}
