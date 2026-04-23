package com.runmarket.api.adapter.out.persistence.mapper;

import com.runmarket.api.adapter.out.persistence.entity.RoleJpaEntity;
import com.runmarket.api.adapter.out.persistence.entity.UserJpaEntity;
import com.runmarket.api.domain.model.Role;
import com.runmarket.api.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity, List<RoleJpaEntity> roles) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .provider(entity.getProvider())
                .providerId(entity.getProviderId())
                .roles(roles.stream().map(this::toRoleDomain).toList())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserJpaEntity toJpaEntity(User user) {
        return UserJpaEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .build();
    }

    public RoleJpaEntity toRoleJpaEntity(Role role) {
        return RoleJpaEntity.builder()
                .userId(role.getUserId())
                .roleType(role.getRoleType())
                .build();
    }

    private Role toRoleDomain(RoleJpaEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .roleType(entity.getRoleType())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
