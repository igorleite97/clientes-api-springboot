package br.com.seatecnologia.clientesapi.dto.request;

import br.com.seatecnologia.clientesapi.model.TipoTelefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para telefone.
 *
 * Aceita número com ou sem máscara, pois o usuário pode digitar
 * de qualquer forma. O service extrai só os dígitos antes de salvar.
 *
 * Exemplos aceitos:
 *   - "(11) 98765-4321" → celular, 11 dígitos
 *   - "1134567890"      → fixo/comercial, 10 dígitos
 *
 * O tipo define a máscara que será usada na resposta:
 *   - CELULAR:              (XX) XXXXX-XXXX
 *   - RESIDENCIAL/COMERCIAL: (XX) XXXX-XXXX
 */
@Getter
@Setter
public class TelefoneRequestDTO {

    @NotBlank(message = "Número de telefone é obrigatório")
    @Pattern(
            // Aceita apenas dois formatos:
            //   1) Somente dígitos: 10 (fixo/comercial) ou 11 (celular)
            //   2) Com máscara: (XX) XXXX-XXXX ou (XX) XXXXX-XXXX
            // Isso evita que strings como "(((((((((()" passem na validação.
            regexp = "^\\d{10,11}$|^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$",
            message = "Telefone inválido. Use 10 dígitos (fixo/comercial) ou 11 dígitos (celular), com ou sem máscara"
    )
    private String numero;

    @NotNull(message = "Tipo de telefone é obrigatório (RESIDENCIAL, COMERCIAL ou CELULAR)")
    private TipoTelefone tipo;
}
