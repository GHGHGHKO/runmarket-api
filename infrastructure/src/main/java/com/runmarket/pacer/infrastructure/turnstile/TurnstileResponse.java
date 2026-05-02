package com.runmarket.pacer.infrastructure.turnstile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record TurnstileResponse(
        boolean success,
        @JsonProperty("error-codes") List<String> errorCodes
) {}
