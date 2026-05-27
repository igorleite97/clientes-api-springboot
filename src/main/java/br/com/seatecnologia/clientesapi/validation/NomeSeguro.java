package br.com.seatecnologia.clientesapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Garante que o nome não contém caracteres maliciosos (XSS/injeção).
 */
@Documented
@Constraint(validatedBy = NomeSeguroValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NomeSeguro {

    String message() default "Nome contém caracteres inválidos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}