package br.com.seatecnologia.clientesapi.client;

import br.com.seatecnologia.clientesapi.dto.ViaCepResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client HTTP declarativo para a API pública ViaCEP.
 *
 * O Feign transforma essa interface em uma implementação real em tempo de execução.
 * Não preciso de RestTemplate, WebClient nem HttpURLConnection — só defino o contrato.
 *
 * URL base vem do application.properties:
 *   spring.cloud.openfeign.client.config.viacep.url=https://viacep.com.br
 *
 * Uso: injeto ViaCepClient no service e chamo buscarPorCep("01310100")
 * O Feign faz GET https://viacep.com.br/ws/01310100/json/ e mapeia para o DTO.
 */
@FeignClient(name = "viacep", url = "${spring.cloud.openfeign.client.config.viacep.url}")
public interface ViaCepClient {

    @GetMapping("/ws/{cep}/json/")
    ViaCepResponseDTO buscarPorCep(@PathVariable("cep") String cep);
}