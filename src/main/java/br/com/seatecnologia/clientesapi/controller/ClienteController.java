package br.com.seatecnologia.clientesapi.controller;

import br.com.seatecnologia.clientesapi.dto.request.ClienteRequestDTO;
import br.com.seatecnologia.clientesapi.dto.response.ClienteResponseDTO;
import br.com.seatecnologia.clientesapi.dto.response.EnderecoResponseDTO;
import br.com.seatecnologia.clientesapi.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para gerenciamento de clientes.
 *
 * Controle de acesso (definido no SecurityConfig):
 *   - GET  → usuários com role USER ou ADMIN
 *   - POST, PUT, DELETE → somente ADMIN
 *
 * Todos os dados sensíveis (CPF, CEP, telefone) já chegam mascarados
 * no response, a limpeza/mascaramento é responsabilidade do service.
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "CRUD de clientes")
@SecurityRequirement(name = "basicAuth")
public class ClienteController {

    private final ClienteService clienteService;

    // ----------------------------------------------------------------
    //  LEITURA — acessível por USER e ADMIN
    // ----------------------------------------------------------------

    @Operation(summary = "Listar todos os clientes")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // ----------------------------------------------------------------
    //  ESCRITA — somente ADMIN
    // ----------------------------------------------------------------

    @Operation(summary = "Cadastrar novo cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado"),
            @ApiResponse(responseCode = "422", description = "CEP não encontrado")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@RequestBody @Valid ClienteRequestDTO dto) {
        ClienteResponseDTO resposta = clienteService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @Operation(summary = "Atualizar dados do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "CPF já pertence a outro cliente"),
            @ApiResponse(responseCode = "422", description = "CEP não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.atualizar(id, dto));
    }

    @Operation(summary = "Excluir cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    //  UTILITÁRIO — consulta de CEP via ViaCEP
    // ----------------------------------------------------------------

    @Operation(summary = "Consultar endereço pelo CEP (ViaCEP)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CEP encontrado, endereço retornado"),
            @ApiResponse(responseCode = "422", description = "CEP inválido ou não encontrado")
    })
    @GetMapping("/cep/{cep}")
    public ResponseEntity<EnderecoResponseDTO> consultarCep(@PathVariable String cep) {
        return ResponseEntity.ok(clienteService.consultarCep(cep));
    }
}
