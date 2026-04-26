package com.runmarket.api.domain.exception;

public class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException() {
        super("유효하지 않거나 만료된 인증 링크입니다.");
    }
}
