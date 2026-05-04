package br.com.seatecnologia.clientesapi.repository;

import br.com.seatecnologia.clientesapi.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Cliente.
 *
 * O Spring Data gera a implementação em tempo de execução
 * não preciso escrever nenhuma query para os métodos básicos.
 *
 * Os métodos derivados (existsByCpf, findByCpf) são traduzidos
 * automaticamente para SQL pelo nome: "find By Cpf" → WHERE cpf = ?
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Usado antes de cadastrar para garantir CPF único
    boolean existsByCpf(String cpf);

    Optional<Cliente> findByCpf(String cpf);
}
