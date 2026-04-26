package com.runmarket.api.domain.exception;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException() {
        super("Email address not verified. Please check your email for the verification link.");
    }
}
