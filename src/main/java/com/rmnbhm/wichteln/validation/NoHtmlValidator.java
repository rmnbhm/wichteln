package com.rmnbhm.wichteln.validation;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoHtmlValidator implements ConstraintValidator<NoHtml, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoHtmlValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String htmlEscaped = StringEscapeUtils.escapeHtml4(value);
        // Based on the simple assumption that the escaped string needs to be longer due to the nature of escaping,
        // e.g. "<" turns to "&lt;".
        boolean containsHtml = htmlEscaped.length() > value.length();
        if (containsHtml) {
            LOGGER.warn(
                    "User tried to enter description containing Java control characters: {} (escaped)", htmlEscaped
            );
            context
                    .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
        }
        return !containsHtml;
    }
}
