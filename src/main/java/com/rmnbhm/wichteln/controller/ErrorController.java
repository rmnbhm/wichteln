package com.rmnbhm.wichteln.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static com.rmnbhm.wichteln.config.WebConfig.ERROR_VIEW;

@ControllerAdvice
public class ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(value = Throwable.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        LOGGER.error("Encountered exception while requesting {}: {}", req.getRequestURI(), e.getMessage());
        return new ModelAndView(ERROR_VIEW, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
