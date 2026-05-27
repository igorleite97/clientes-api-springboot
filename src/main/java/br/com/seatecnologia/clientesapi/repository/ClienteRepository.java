package br.com.seatecnologia.clientesapi.repository;

import br.com.seatecnologia.clientesapi.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Cliente.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Verificação rápida de unicidade antes de tentar persistir
    boolean existsByCpf(String cpf);

    // Necessário no update: permite verificar se o CPF pertence a outro cliente
    Optional<Cliente> findByCpf(String cpf);
}
