package com.runmarket.pacer.domain.port.out.verification;

public interface CaptchaVerificationPort {
    void verify(String token);
}
