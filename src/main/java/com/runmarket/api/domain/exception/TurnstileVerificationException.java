package com.runmarket.api.domain.exception;

public class TurnstileVerificationException extends RuntimeException {
    public TurnstileVerificationException() {
        super("보안 인증에 실패했습니다. 다시 시도해주세요.");
    }
}
