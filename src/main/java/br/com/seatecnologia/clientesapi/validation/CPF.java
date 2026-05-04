package br.com.seatecnologia.clientesapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação customizada para validação de CPF.
 *
 * Como usar:
 *   @CPF
 *   private String cpf;
 *
 * Ela valida tanto o formato (11 dígitos) quanto o algoritmo matemático
 * que verifica se o CPF é matematicamente válido — não só se tem 11 números.
 */
@Documented
@Constraint(validatedBy = CPFValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CPF {

    String message() default "CPF inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
