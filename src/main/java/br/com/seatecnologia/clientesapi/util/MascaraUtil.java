package br.com.seatecnologia.clientesapi.util;

import br.com.seatecnologia.clientesapi.model.TipoTelefone;

/**
 * Utilitário centralizado para aplicar e remover máscaras.
 *
 * Regra de ouro do projeto:
 *   - REMOVE a máscara antes de persistir (só dígitos no banco)
 *   - APLICA a máscara ao montar o DTO de resposta
 */
public final class MascaraUtil {

    private MascaraUtil() {}

    /**
     * Remove todos os caracteres não numéricos de uma string.
     * Usado para limpar CPF, CEP e telefone antes de salvar.
     *
     * Ex: "123.456.789-01" → "12345678901"
     */
    public static String apenasDigitos(String valor) {
        if (valor == null) return null;
        return valor.replaceAll("\\D", "");
    }

    /**
     * Aplica máscara ao CPF.
     * Ex: "12345678901" → "123.456.789-01"
     */
    public static String cpf(String cpfSemMascara) {
        return cpfSemMascara.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Aplica máscara ao CEP.
     * Ex: "01310100" → "01310-100"
     */
    public static String cep(String cepSemMascara) {
        return cepSemMascara.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }

    /**
     * Aplica máscara ao telefone conforme o tipo.
     *
     * Celular (11 dígitos):              "11987654321" → "(11) 98765-4321"
     * Residencial/Comercial (10 dígitos): "1134567890"  → "(11) 3456-7890"
     */
    public static String telefone(String numeroSemMascara, TipoTelefone tipo) {
        if (tipo == TipoTelefone.CELULAR) {
            return numeroSemMascara.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        return numeroSemMascara.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
    }
}
