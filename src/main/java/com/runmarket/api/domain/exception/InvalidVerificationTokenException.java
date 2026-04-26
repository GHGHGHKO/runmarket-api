package com.runmarket.api.domain.exception;

public class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException() {
        super("Verification token is invalid or has expired");
    }
}
