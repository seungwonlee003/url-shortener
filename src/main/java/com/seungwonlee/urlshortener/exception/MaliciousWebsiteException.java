package com.seungwonlee.urlshortener.exception;

public class MaliciousWebsiteException extends RuntimeException {
    public MaliciousWebsiteException(String message) {
        super(message);
    }
}
