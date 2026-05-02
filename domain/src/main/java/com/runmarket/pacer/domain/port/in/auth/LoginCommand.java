package com.runmarket.pacer.domain.port.in.auth;

public record LoginCommand(String email, String password, String captchaToken) {}
