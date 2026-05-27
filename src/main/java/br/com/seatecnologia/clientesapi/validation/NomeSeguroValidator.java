package br.com.seatecnologia.clientesapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida que o nome contém apenas letras, espaços e números.
 *
 * Bloqueia caracteres usados em ataques XSS e injeção:
 *   < > " ' ; & % $ # @ ! = ( ) { } [ ] \ /
 *
 * Exemplos rejeitados:
 *   - "<script>alert(1)</script>"
 *   - "João'; DROP TABLE tb_cliente--"
 *   - "Admin<img src=x>"
 */
public class NomeSeguroValidator implements ConstraintValidator<NomeSeguro, String> {

    // Permite apenas letras (incluindo acentuadas), espaços e números
    private static final String PADRAO_SEGURO = "^[\\p{L}\\p{N} ]+$";

    @Override
    public boolean isValid(String nome, ConstraintValidatorContext context) {
        if (nome == null || nome.isBlank()) {
            // @NotBlank já cobre isso — retornamos true para não duplicar o erro
            return true;
        }
        return nome.matches(PADRAO_SEGURO);
    }
}