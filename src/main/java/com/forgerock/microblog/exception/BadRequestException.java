package com.forgerock.microblog.exception;

/**
 * Client submitted invalid request
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg) {

        super(msg);
    }
}
