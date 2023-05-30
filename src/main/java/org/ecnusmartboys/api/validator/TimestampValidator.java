package org.ecnusmartboys.api.validator;


import org.ecnusmartboys.api.annotation.Timestamp;
import org.ecnusmartboys.infrastructure.utils.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimestampValidator implements ConstraintValidator<Timestamp, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
        return Validator.validateTimestamp(value);
    }
}
