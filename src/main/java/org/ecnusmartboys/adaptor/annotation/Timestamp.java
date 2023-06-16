package org.ecnusmartboys.adaptor.annotation;

import org.ecnusmartboys.adaptor.validator.TimestampValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimestampValidator.class)
public @interface Timestamp {

    String message() default "时间戳范围在2023年1月1日到2024年12月31日";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
