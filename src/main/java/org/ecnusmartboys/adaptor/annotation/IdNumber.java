package org.ecnusmartboys.adaptor.annotation;

import org.ecnusmartboys.adaptor.validator.IdNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdNumberValidator.class)
public @interface IdNumber {

    String message() default "身份证格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
