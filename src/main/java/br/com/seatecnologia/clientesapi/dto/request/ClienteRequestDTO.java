package br.com.seatecnologia.clientesapi.dto.request;

import br.com.seatecnologia.clientesapi.validation.CPF;
import br.com.seatecnologia.clientesapi.validation.NomeSeguro;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO de entrada para criar ou atualizar um cliente.
 *
 * Cada campo aqui é validado ANTES de chegar no service,
 * se algo estiver errado, o Spring retorna 400 com o erro específico.
 *
 * CPF: aceita com ou sem máscara (ex: 123.456.789-01 ou 12345678901).
 *      A limpeza dos dígitos é feita no service antes de persistir.
 */
@Getter
@Setter
public class ClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @NomeSeguro
    private String nome;

    @NotBlank( message = "CPF é obrigatório")
    @CPF
    private String cpf;

    // @NotNull garante que o campo não seja omitido; @Valid propaga validação internamente
    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private EnderecoRequestDTO endereco;

    // @NotEmpty garante ao menos um telefone; @Valid valida cada item da lista
    @NotEmpty(message = "Informe pelo menos um telefone")
    @Valid
    private List<TelefoneRequestDTO> telefones;

    @NotEmpty(message = "Informe pelo menos um e-mail")
    @Valid
    private List<EmailRequestDTO> emails;
}
