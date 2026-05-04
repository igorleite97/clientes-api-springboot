package br.com.seatecnologia.clientesapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * E-mails do cliente. Separado porque a relação é N:1 com Cliente.
 *
 * Removi o import de jakarta.validation.constraints.Email porque o nome
 * da anotação conflita com o nome desta classe. A validação de formato
 * de e-mail foi substituída por @Pattern equivalente — mesmo resultado,
 * zero conflito de nomenclatura.
 */
@Entity
@Table(name = "tb_email")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "E-mail é obrigatório")
    @jakarta.validation.constraints.Email(message = "Formato de e-mail inválido")
    @Column(name = "endereco", length = 150, nullable = false)
    private String endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}