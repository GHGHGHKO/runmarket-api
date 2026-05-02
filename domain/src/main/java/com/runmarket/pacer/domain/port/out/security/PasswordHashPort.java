package com.runmarket.pacer.domain.port.out.security;

public interface PasswordHashPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
