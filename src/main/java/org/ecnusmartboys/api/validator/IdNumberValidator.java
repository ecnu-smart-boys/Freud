package org.ecnusmartboys.api.validator;

import org.ecnusmartboys.api.annotation.IdNumber;
import org.ecnusmartboys.infrastructure.utils.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdNumberValidator implements ConstraintValidator<IdNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.validateID(value);
    }
}
