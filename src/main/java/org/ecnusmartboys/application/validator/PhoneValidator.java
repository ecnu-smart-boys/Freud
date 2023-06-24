package org.ecnusmartboys.application.validator;

import org.ecnusmartboys.adaptor.annotation.Phone;
import org.ecnusmartboys.infrastructure.utils.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.validatePhone(value);
    }
}
