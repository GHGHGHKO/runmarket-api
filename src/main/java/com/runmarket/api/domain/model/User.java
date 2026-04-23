package com.runmarket.api.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class User {
    private UUID id;
    private String email;
    private String password;
    private String nickname;
    private AuthProvider provider;
    private String providerId;
    private List<Role> roles;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
