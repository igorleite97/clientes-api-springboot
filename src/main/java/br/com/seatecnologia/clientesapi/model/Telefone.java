package br.com.seatecnologia.clientesapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cada cliente pode ter múltiplos telefones — por isso é uma entity separada.
 *
 * O número é persistido SEM máscara (só dígitos).
 * A máscara é aplicada apenas no DTO de resposta, conforme o tipo:
 *   - RESIDENCIAL/COMERCIAL: (XX) XXXX-XXXX
 *   - CELULAR:               (XX) XXXXX-XXXX
 */
@Entity
@Table(name = "tb_telefone")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Telefone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Aceita 10 (fixo) ou 11 (celular) dígitos numéricos.
     * O tipo define qual formato de máscara usar na exibição.
     */
    @NotBlank(message = "Número de telefone é obrigatório")
    @Pattern(
            regexp = "\\d{10,11}",
            message = "Telefone deve conter 10 dígitos (fixo) ou 11 dígitos (celular), apenas números"
    )
    @Column(name = "numero", length = 11, nullable = false)
    private String numero;

    @NotNull(message = "Tipo de telefone é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20, nullable = false)
    private TipoTelefone tipo;

    /**
     * Relacionamento com Cliente.
     * LAZY: só carrego o cliente se explicitamente precisar — evita N+1.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}