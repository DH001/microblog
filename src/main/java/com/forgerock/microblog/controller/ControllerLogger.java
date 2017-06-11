package com.forgerock.microblog.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Logs calls to the controller methods
 */
@Aspect
@Component
public class ControllerLogger {

    private final static Logger LOG = LoggerFactory.getLogger(ControllerLogger.class);

    @Before("execution(* com.forgerock.microblog.controller.*.*(..))")
    public void log(final JoinPoint point) {

        final String args = Arrays.stream(point.getArgs())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        LOG.info("name={} args=({})", point.getSignature().getName(), args);
    }
}
