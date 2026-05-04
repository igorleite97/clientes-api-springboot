package br.com.seatecnologia.clientesapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Endereço embutido dentro da tabela TB_CLIENTE.
 *
 * Usei @Embeddable porque endereço não faz sentido existir sem um cliente.
 * Não precisa de ID próprio nem de tabela separada.
 *
 * Fluxo de uso:
 *   1. Recebo o CEP do usuário
 *   2. Consulto a ViaCEP (no service) e preencho logradouro/bairro/cidade/uf
 *   3. O usuário pode sobrescrever qualquer campo (regra do desafio)
 *   4. CEP é persistido SEM máscara → exibido COM máscara no DTO de resposta
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    /**
     * Apenas dígitos — a máscara (XXXXX-XXX) é aplicada no DTO de resposta.
     * Regex garante 8 dígitos numéricos antes de persistir.
     */
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos numéricos (sem máscara)")
    @Column(name = "cep", length = 8)
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Column(name = "logradouro", length = 200)
    private String logradouro;

    // Complemento é opcional conforme o desafio
    @Column(name = "complemento", length = 100)
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Column(name = "bairro", length = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Column(name = "cidade", length = 100)
    private String cidade;

    @NotBlank(message = "UF é obrigatória")
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve ter exatamente 2 letras maiúsculas")
    @Column(name = "uf", length = 2)
    private String uf;
}