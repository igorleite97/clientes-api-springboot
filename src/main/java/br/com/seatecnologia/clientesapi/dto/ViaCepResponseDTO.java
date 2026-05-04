package br.com.seatecnologia.clientesapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que mapeia exatamente o JSON que a API ViaCEP retorna.
 *
 * Os nomes dos campos seguem o padrão da ViaCEP (logradouro, bairro, etc.).
 * @JsonProperty só é necessário quando o nome do campo Java difere do JSON.
 *
 * Se o CEP não existir, a ViaCEP retorna: {"erro": "true"}
 * Por isso tenho o campo 'erro' aqui — uso ele no service para lançar exceção.
 */
@Getter
@Setter
@NoArgsConstructor
public class ViaCepResponseDTO {

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;

    @JsonProperty("localidade")
    private String cidade;

    private String uf;

    // Presente apenas quando o CEP não é encontrado: {"erro": "true"}
    private String erro;

    public boolean isErro() {
        return "true".equals(this.erro);
    }
}