package com.runmarket.api.domain.exception;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException() {
        super("이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요.");
    }
}
