package com.runmarket.pacer.domain.event;

public record EmailVerificationEvent(String email, String verificationLink) {}
