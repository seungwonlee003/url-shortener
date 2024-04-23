package com.seungwonlee.urlshortener.exception;

public class CustomShortUrlAlreadyExistsException extends RuntimeException {

    public CustomShortUrlAlreadyExistsException(String message) {
        super(message);
    }
}
