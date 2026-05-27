package br.com.seatecnologia.clientesapi.client;

import br.com.seatecnologia.clientesapi.dto.ViaCepResponseDTO;
import org.springframework.cloud. openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client HTTP declarativo para a API pública ViaCEP.
 *
 * URL base configurada em:
 *   spring.cloud.openfeign.client.config.viacep.url=https://viacep.com.br
 */
@FeignClient(name = "viacep", url = "${spring.cloud.openfeign.client.config.viacep.url}")
public interface ViaCepClient {

    @GetMapping("/ws/{cep}/json/")
    ViaCepResponseDTO buscarPorCep(@PathVariable("cep") String cep);
}