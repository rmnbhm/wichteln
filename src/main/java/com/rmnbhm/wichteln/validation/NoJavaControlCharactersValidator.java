package com.rmnbhm.wichteln.validation;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class NoJavaControlCharactersValidator implements ConstraintValidator<NoJavaControlCharacters, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoJavaControlCharactersValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // This validator is not concerned with null checks.
        if (isNull(value)) {
            return true;
        }
        String javaEscaped = StringEscapeUtils.escapeJava(value);
        // Based on the simple assumption that the escaped string needs to be longer due to the nature of escaping,
        // e.g. "\n" turns to "\\n".
        boolean containsJavaControlCharacters = javaEscaped.length() > value.length();
        if (containsJavaControlCharacters) {
            LOGGER.warn(
                    "User tried to enter description containing Java control characters: {} (escaped)", javaEscaped
            );
            context
                    .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
        }
        return !containsJavaControlCharacters;
    }
}
