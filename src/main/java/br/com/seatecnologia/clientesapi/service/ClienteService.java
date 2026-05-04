package br.com.seatecnologia.clientesapi.service;

import br.com.seatecnologia.clientesapi.client.ViaCepClient;
import br.com.seatecnologia.clientesapi.dto.ViaCepResponseDTO;
import br.com.seatecnologia.clientesapi.dto.request.ClienteRequestDTO;
import br.com.seatecnologia.clientesapi.dto.request.EmailRequestDTO;
import br.com.seatecnologia.clientesapi.dto.request.EnderecoRequestDTO;
import br.com.seatecnologia.clientesapi.dto.request.TelefoneRequestDTO;
import br.com.seatecnologia.clientesapi.dto.response.ClienteResponseDTO;
import br.com.seatecnologia.clientesapi.dto.response.EmailResponseDTO;
import br.com.seatecnologia.clientesapi.dto.response.EnderecoResponseDTO;
import br.com.seatecnologia.clientesapi.dto.response.TelefoneResponseDTO;
import br.com.seatecnologia.clientesapi.exception.CepFormatoInvalidoException;
import br.com.seatecnologia.clientesapi.exception.CepInvalidoException;
import br.com.seatecnologia.clientesapi.exception.ClienteNaoEncontradoException;
import br.com.seatecnologia.clientesapi.exception.CpfJaCadastradoException;
import feign.FeignException;
import br.com.seatecnologia.clientesapi.model.Cliente;
import br.com.seatecnologia.clientesapi.model.Email;
import br.com.seatecnologia.clientesapi.model.Endereco;
import br.com.seatecnologia.clientesapi.model.Telefone;
import br.com.seatecnologia.clientesapi.repository.ClienteRepository;
import br.com.seatecnologia.clientesapi.util.MascaraUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Camada de negócio, aqui vivem as regras do desafio.
 *
 * Responsabilidades:
 *   1. Orquestrar a consulta ao ViaCEP e mesclar com os dados do usuário
 *   2. Remover máscaras antes de persistir (CPF, CEP, telefone)
 *   3. Aplicar máscaras ao montar os DTOs de resposta
 *   4. Garantir unicidade de CPF
 *   5. Validar que o CEP existe de verdade
 *
 * @RequiredArgsConstructor gera o construtor com os campos final —
 * é a forma recomendada de injeção de dependências no Spring.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ViaCepClient viaCepClient;

    // ----------------------------------------------------------------
    //  LEITURA
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
        return toResponseDTO(cliente);
    }

    /**
     * Consulta de CEP exposta como endpoint público (GET /api/clientes/cep/{cep}).
     * Útil para o front-end pré-preencher o formulário de endereço.
     *
     * Fluxo de tratamento de erros:
     *   1. CEP com formato errado (≠ 8 dígitos) → CepFormatoInvalidoException → 400
     *   2. ViaCEP retorna {"erro":"true"} (CEP não existe) → CepInvalidoException → 422
     *   3. ViaCEP retorna HTTP 4xx/5xx (ex: 400 para CEP mal-formado) → FeignException
     *      capturada aqui e traduzida para CepInvalidoException → 422
     */
    @Transactional(readOnly = true)
    public EnderecoResponseDTO consultarCep(String cep) {
        String cepLimpo = MascaraUtil.apenasDigitos(cep);

        // Primeira barreira: se nem 8 dígitos tem, nem adianta chamar a ViaCEP
        if (cepLimpo == null || cepLimpo.length() != 8) {
            throw new CepFormatoInvalidoException(cep);
        }

        ViaCepResponseDTO viaCepResponse = chamarViaCep(cepLimpo, cep);

        // Segunda barreira: ViaCEP respondeu 200 mas o CEP não existe
        if (viaCepResponse.isErro()) {
            throw new CepInvalidoException(cep);
        }

        return EnderecoResponseDTO.builder()
                .cep(MascaraUtil.cep(cepLimpo))
                .logradouro(viaCepResponse.getLogradouro())
                .complemento(viaCepResponse.getComplemento())
                .bairro(viaCepResponse.getBairro())
                .cidade(viaCepResponse.getCidade())
                .uf(viaCepResponse.getUf())
                .build();
    }

    // ----------------------------------------------------------------
    //  ESCRITA
    // ----------------------------------------------------------------

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        String cpfLimpo = MascaraUtil.apenasDigitos(dto.getCpf());

        if (clienteRepository.existsByCpf(cpfLimpo)) {
            throw new CpfJaCadastradoException(MascaraUtil.cpf(cpfLimpo));
        }

        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .cpf(cpfLimpo)
                .endereco(montarEndereco(dto.getEndereco()))
                .build();

        adicionarTelefones(cliente, dto.getTelefones());
        adicionarEmails(cliente, dto.getEmails());

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        String cpfLimpo = MascaraUtil.apenasDigitos(dto.getCpf());

        // Permite manter o mesmo CPF; só bloqueia se for de outro cliente
        boolean cpfPertenceAOutro = clienteRepository.findByCpf(cpfLimpo)
                .map(c -> !c.getId().equals(id))
                .orElse(false);

        if (cpfPertenceAOutro) {
            throw new CpfJaCadastradoException(MascaraUtil.cpf(cpfLimpo));
        }

        cliente.setNome(dto.getNome());
        cliente.setCpf(cpfLimpo);
        cliente.setEndereco(montarEndereco(dto.getEndereco()));

        // Limpa e recarrega telefones/emails — orphanRemoval cuida da deleção no banco
        cliente.getTelefones().clear();
        cliente.getEmails().clear();

        adicionarTelefones(cliente, dto.getTelefones());
        adicionarEmails(cliente, dto.getEmails());

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNaoEncontradoException(id);
        }
        clienteRepository.deleteById(id);
    }

    // ----------------------------------------------------------------
    //  MÉTODOS AUXILIARES PRIVADOS
    // ----------------------------------------------------------------

    /**
     * Consulta o ViaCEP e monta o Endereco mesclando os dados:
     *   - Campos que o usuário informou → prevalecem
     *   - Campos que o usuário deixou em branco → usamos o retorno da ViaCEP
     *
     * Isso implementa a regra: "o usuário pode alterar os dados que vieram do ViaCEP".
     *
     * O CEP já chega com 8 dígitos validados pelo @Pattern do EnderecoRequestDTO,
     * então aqui só precisamos tratar erros da chamada HTTP em si.
     */
    private Endereco montarEndereco(EnderecoRequestDTO dto) {
        String cepLimpo = MascaraUtil.apenasDigitos(dto.getCep());

        ViaCepResponseDTO viaCep = chamarViaCep(cepLimpo, dto.getCep());
        if (viaCep.isErro()) {
            throw new CepInvalidoException(dto.getCep());
        }

        // Preferência para o valor do usuário; fallback para o retorno da ViaCEP
        String logradouro = StringUtils.hasText(dto.getLogradouro()) ? dto.getLogradouro() : viaCep.getLogradouro();
        String bairro     = StringUtils.hasText(dto.getBairro())     ? dto.getBairro()     : viaCep.getBairro();
        String cidade     = StringUtils.hasText(dto.getCidade())     ? dto.getCidade()     : viaCep.getCidade();
        String uf         = StringUtils.hasText(dto.getUf())         ? dto.getUf()         : viaCep.getUf();

        return Endereco.builder()
                .cep(cepLimpo)
                .logradouro(logradouro)
                .complemento(dto.getComplemento())
                .bairro(bairro)
                .cidade(cidade)
                .uf(uf)
                .build();
    }

    /**
     * Encapsula a chamada ao Feign client com tratamento de erro centralizado.
     *
     * Por que centralizar aqui em vez de repetir try-catch em cada método?
     * Porque tanto consultarCep() quanto montarEndereco() chamam a ViaCEP —
     * ter o tratamento em um único lugar evita duplicação e garante
     * comportamento consistente para qualquer chamada ao serviço externo.
     *
     * FeignException é lançada quando a ViaCEP retorna HTTP não-2xx.
     * Isso acontece, por exemplo, quando o CEP tem 8 dígitos mas está
     * em um formato que a ViaCEP não aceita.
     * Nesse caso tratamos como "CEP inválido" → 422.
     */
    private ViaCepResponseDTO chamarViaCep(String cepLimpo, String cepOriginal) {
        try {
            return viaCepClient.buscarPorCep(cepLimpo);
        } catch (FeignException e) {
            // A ViaCEP respondeu com erro HTTP (ex: 400 para formato não aceito,
            // ou 5xx se o serviço externo estiver fora do ar).
            // Do ponto de vista do nosso cliente, o resultado é o mesmo: CEP inválido.
            throw new CepInvalidoException(cepOriginal);
        }
    }

    private void adicionarTelefones(Cliente cliente, List<TelefoneRequestDTO> dtos) {
        dtos.forEach(dto -> {
            Telefone telefone = Telefone.builder()
                    .numero(MascaraUtil.apenasDigitos(dto.getNumero()))
                    .tipo(dto.getTipo())
                    .build();
            cliente.adicionarTelefone(telefone);
        });
    }

    private void adicionarEmails(Cliente cliente, List<EmailRequestDTO> dtos) {
        dtos.forEach(dto -> {
            Email email = Email.builder()
                    .endereco(dto.getEndereco())
                    .build();
            cliente.adicionarEmail(email);
        });
    }

    /**
     * Converte a entidade Cliente para o DTO de resposta,
     * aplicando todas as máscaras exigidas pelo desafio.
     */
    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        Endereco end = cliente.getEndereco();

        EnderecoResponseDTO enderecoResponse = EnderecoResponseDTO.builder()
                .cep(MascaraUtil.cep(end.getCep()))
                .logradouro(end.getLogradouro())
                .complemento(end.getComplemento())
                .bairro(end.getBairro())
                .cidade(end.getCidade())
                .uf(end.getUf())
                .build();

        List<TelefoneResponseDTO> telefonesResponse = cliente.getTelefones().stream()
                .map(tel -> TelefoneResponseDTO.builder()
                        .id(tel.getId())
                        .numero(MascaraUtil.telefone(tel.getNumero(), tel.getTipo()))
                        .tipo(tel.getTipo())
                        .build())
                .toList();

        List<EmailResponseDTO> emailsResponse = cliente.getEmails().stream()
                .map(email -> EmailResponseDTO.builder()
                        .id(email.getId())
                        .endereco(email.getEndereco())
                        .build())
                .toList();

        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(MascaraUtil.cpf(cliente.getCpf()))
                .endereco(enderecoResponse)
                .telefones(telefonesResponse)
                .emails(emailsResponse)
                .build();
    }
}
