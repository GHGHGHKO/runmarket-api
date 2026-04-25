package com.runmarket.api.domain.event;

public record EmailVerificationEvent(String email, String verificationLink) {}
