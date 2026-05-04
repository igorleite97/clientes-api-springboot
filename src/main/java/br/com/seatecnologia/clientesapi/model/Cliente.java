package br.com.seatecnologia.clientesapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade principal do sistema.
 *
 * Decisões arquiteturais tomadas aqui:
 *   - Endereço como @Embeddable: evita JOIN desnecessário, endereço não existe sem cliente
 *   - Telefones/Emails como @OneToMany com CascadeType.ALL: o ciclo de vida dos filhos
 *     é totalmente controlado pelo pai (salvar cliente já salva telefones e emails)
 *   - orphanRemoval = true: se removo um telefone da lista e salvo o cliente,
 *     o Hibernate deleta o registro órfão automaticamente
 *
 * CPF é persistido SEM máscara. A máscara XXX.XXX.XXX-XX é gerada no DTO de resposta.
 */
@Entity
@Table(name = "tb_cliente")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome: 3 a 100 caracteres, letras/espaços/números.
     * A anotação @NomeSeguro (que vamos criar) verifica injeção de scripts.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    /**
     * CPF persistido como string de 11 dígitos numéricos (sem máscara, sem pontos).
     * A anotação @CPF valida o algoritmo matemático (não só o formato).
     */
    @Column(name = "cpf", length = 11, nullable = false, unique = true)
    private String cpf;

    /**
     * @Embedded: os campos de Endereco viram colunas direto em TB_CLIENTE.
     * @Valid: propaga a validação para dentro do objeto embutido.
     */
    @Embedded
    @Valid
    private Endereco endereco;

    /**
     * CascadeType.ALL: qualquer operação no Cliente se propaga para os telefones.
     * orphanRemoval: remove do banco qualquer telefone que sair da lista.
     */
    // fetch = LAZY: os telefones só são carregados do banco quando acessados.
    // Evita trazer dados desnecessários em consultas que não precisam dos filhos.
    @OneToMany(
            mappedBy = "cliente",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Telefone> telefones = new ArrayList<>();

    @OneToMany(
            mappedBy = "cliente",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Email> emails = new ArrayList<>();

    /**
     * Métodos helper para manter a consistência bidirecional do relacionamento.
     *
     * Por que preciso disso?
     * Se eu só fizer cliente.getTelefones().add(tel), o campo telefone.cliente
     * fica null — e o Hibernate não sabe de qual cliente esse telefone é filho.
     * Esses métodos garantem os dois lados do relacionamento de uma vez.
     */
    public void adicionarTelefone(Telefone telefone) {
        telefone.setCliente(this);
        this.telefones.add(telefone);
    }

    public void adicionarEmail(Email email) {
        email.setCliente(this);
        this.emails.add(email);
    }
}