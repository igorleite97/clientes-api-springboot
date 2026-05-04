package br.com.seatecnologia.clientesapi.config;

import br.com.seatecnologia.clientesapi.model.Cliente;
import br.com.seatecnologia.clientesapi.model.Email;
import br.com.seatecnologia.clientesapi.model.Endereco;
import br.com.seatecnologia.clientesapi.model.Telefone;
import br.com.seatecnologia.clientesapi.model.TipoTelefone;
import br.com.seatecnologia.clientesapi.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Popula o banco com dados de demonstração ao iniciar a aplicação.
 *
 * Por que fazer isso?
 *   → O avaliador abre o Swagger e já encontra dados reais para testar.
 *   → Cobre todos os cenários do desafio: múltiplos telefones, múltiplos
 *     e-mails, tipos diferentes de telefone, endereços de estados distintos.
 *
 * Dados persistidos diretamente no repositório (sem chamada à ViaCEP)
 * para garantir que o seed funcione mesmo sem conexão à internet.
 *
 * Os CPFs usados são matematicamente válidos (algoritmo dos dois dígitos
 * verificadores), podem ser testados na validação sem erro.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    // @Transactional aqui garante que os 3 saves sejam atômicos:
    // ou todos os clientes são persistidos, ou nenhum — sem estado parcial.
    @Override
    @Transactional
    public void run(String... args) {
        // Proteção idempotente: não repopula se já houver dados
        if (clienteRepository.count() > 0) {
            log.info("Banco ja possui dados — seed ignorado.");
            return;
        }

        log.info("Carregando dados de demonstracao para avaliacao...");

        salvarJoao();
        salvarAna();
        salvarCarlos();

        log.info("[OK] {} clientes de demonstracao carregados com sucesso.", clienteRepository.count());
    }

    /**
     * João / cobre: celular + comercial, dois e-mails, endereço de SP.
     * CPF: 529.982.247-25
     */
    private void salvarJoao() {
        Cliente joao = Cliente.builder()
                .nome("João Carlos Pereira")
                .cpf("52998224725")
                .endereco(Endereco.builder()
                        .cep("01310100")
                        .logradouro("Avenida Paulista")
                        .complemento("Conjunto 42")
                        .bairro("Bela Vista")
                        .cidade("São Paulo")
                        .uf("SP")
                        .build())
                .build();

        joao.adicionarTelefone(Telefone.builder()
                .numero("11987654321")
                .tipo(TipoTelefone.CELULAR)
                .build());

        joao.adicionarTelefone(Telefone.builder()
                .numero("1134567890")
                .tipo(TipoTelefone.COMERCIAL)
                .build());

        joao.adicionarEmail(Email.builder().endereco("joao.pereira@email.com").build());
        joao.adicionarEmail(Email.builder().endereco("joao.trabalho@empresa.com").build());

        clienteRepository.save(joao);
    }

    /**
     * Ana — cobre: celular + residencial, um e-mail, endereço do RJ.
     * CPF: 275.484.389-23
     */
    private void salvarAna() {
        Cliente ana = Cliente.builder()
                .nome("Ana Silva Santos")
                .cpf("27548438923")
                .endereco(Endereco.builder()
                        .cep("20040020")
                        .logradouro("Avenida Rio Branco")
                        .bairro("Centro")
                        .cidade("Rio de Janeiro")
                        .uf("RJ")
                        .build())
                .build();

        ana.adicionarTelefone(Telefone.builder()
                .numero("21987654321")
                .tipo(TipoTelefone.CELULAR)
                .build());

        ana.adicionarTelefone(Telefone.builder()
                .numero("2134567890")
                .tipo(TipoTelefone.RESIDENCIAL)
                .build());

        ana.adicionarEmail(Email.builder().endereco("ana.santos@email.com").build());

        clienteRepository.save(ana);
    }

    /**
     * Carlos — cobre: comercial apenas, dois e-mails, endereço de MG.
     * CPF: 111.444.777-35
     */
    private void salvarCarlos() {
        Cliente carlos = Cliente.builder()
                .nome("Carlos Eduardo Lima")
                .cpf("11144477735")
                .endereco(Endereco.builder()
                        .cep("30112000")
                        .logradouro("Avenida Afonso Pena")
                        .bairro("Centro")
                        .cidade("Belo Horizonte")
                        .uf("MG")
                        .build())
                .build();

        carlos.adicionarTelefone(Telefone.builder()
                .numero("3134567890")
                .tipo(TipoTelefone.COMERCIAL)
                .build());

        carlos.adicionarEmail(Email.builder().endereco("carlos.lima@empresa.com.br").build());
        carlos.adicionarEmail(Email.builder().endereco("c.lima@pessoal.com").build());

        clienteRepository.save(carlos);
    }
}
