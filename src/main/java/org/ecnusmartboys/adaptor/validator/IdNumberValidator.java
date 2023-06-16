package org.ecnusmartboys.adaptor.validator;

import org.ecnusmartboys.adaptor.annotation.IdNumber;
import org.ecnusmartboys.infrastructure.utils.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdNumberValidator implements ConstraintValidator<IdNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.validateID(value);
    }
}
