package com.runmarket.pacer.domain.port.in.auth;

public record RegisterCommand(String email, String password, String captchaToken) {}
