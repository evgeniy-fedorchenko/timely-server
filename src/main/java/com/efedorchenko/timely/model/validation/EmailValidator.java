package com.efedorchenko.timely.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {

    private static final String EMAIL_LOCAL_PART = "[a-zA-Z0-9][a-zA-Z0-9._-]{0,62}[a-zA-Z0-9]";
    private static final String EMAIL_SUBDOMAIN_PART = "([a-zA-Z0-9][a-zA-Z0-9_-]{1,14}\\.)";
    private static final String EMAIL_TLD_PART = "([a-z]{2,4})";

    public static final String EMAIL_REGEX =
            "^(?!.*[-._]{2})" + EMAIL_LOCAL_PART + "@" + EMAIL_SUBDOMAIN_PART + "{1,2}" + EMAIL_TLD_PART;

    private Pattern pattern;

    @Override
    public void initialize(Email constraintAnnotation) {
        pattern = Pattern.compile(EMAIL_REGEX);
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || pattern.matcher(value).matches();
    }

    public Pattern getPattern() {
        return pattern != null ? pattern : Pattern.compile(EMAIL_REGEX);
    }
}
