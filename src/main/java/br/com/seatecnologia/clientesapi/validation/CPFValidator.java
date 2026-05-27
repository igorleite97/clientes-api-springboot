package br.com.seatecnologia.clientesapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementação do algoritmo de validação de CPF.
 *
 * O CPF chega aqui já sem máscara (só dígitos), a limpeza foi feita no service.
 *
 * O algoritmo funciona assim:
 *   1. Verifica se tem 11 dígitos
 *   2. Rejeita sequências repetidas (111.111.111-11 é inválido matematicamente)
 *   3. Calcula o 1º dígito verificador
 *   4. Calcula o 2º dígito verificador
 *   5. Compara com os dígitos informados
 */
public class CPFValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) {
            // @NotBlank já cobre isso — retornamos true para não duplicar o erro
            return true;
        }

        // Remove máscara caso venha com ela (proteção extra)
        String apenasDigitos = cpf.replaceAll("\\D", "");

        if (apenasDigitos.length() != 11) {
            return false;
        }

        // Sequências repetidas são matematicamente inválidas mas passariam no algoritmo
        if (apenasDigitos.matches("(\\d)\\1{10}")) {
            return false;
        }

        return validarDigitosVerificadores(apenasDigitos);
    }

    private boolean validarDigitosVerificadores(String cpf) {
        // ---- Cálculo do 1º dígito verificador ----
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;

        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        // ---- Cálculo do 2º dígito verificador ----
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;

        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }
}