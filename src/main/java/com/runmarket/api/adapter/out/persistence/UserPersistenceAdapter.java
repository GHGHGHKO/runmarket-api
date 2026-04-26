package com.runmarket.api.adapter.out.persistence;

import com.runmarket.api.adapter.out.persistence.entity.RoleJpaEntity;
import com.runmarket.api.adapter.out.persistence.entity.UserJpaEntity;
import com.runmarket.api.adapter.out.persistence.mapper.UserMapper;
import com.runmarket.api.adapter.out.persistence.repository.EmailVerificationTokenJpaRepository;
import com.runmarket.api.adapter.out.persistence.repository.RoleJpaRepository;
import com.runmarket.api.adapter.out.persistence.repository.UserJpaRepository;
import com.runmarket.api.domain.model.Role;
import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final EmailVerificationTokenJpaRepository tokenJpaRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(this::toUserWithRoles);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity savedUser = userJpaRepository.save(userMapper.toJpaEntity(user));
        List<RoleJpaEntity> savedRoles = user.getRoles().stream()
                .map(role -> {
                    Role roleWithUserId = Role.builder()
                            .id(role.getId())
                            .userId(savedUser.getId())
                            .roleType(role.getRoleType())
                            .build();
                    return roleJpaRepository.save(userMapper.toRoleJpaEntity(roleWithUserId));
                })
                .toList();
        return userMapper.toDomain(savedUser, savedRoles);
    }

    @Override
    @Transactional
    public void updateVerified(UUID userId) {
        userJpaRepository.findById(userId).ifPresent(UserJpaEntity::verify);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId) {
        tokenJpaRepository.deleteByUserId(userId);
        roleJpaRepository.deleteAllByUserId(userId);
        userJpaRepository.deleteById(userId);
    }

    private User toUserWithRoles(UserJpaEntity entity) {
        List<RoleJpaEntity> roles = roleJpaRepository.findAllByUserId(entity.getId());
        return userMapper.toDomain(entity, roles);
    }
}
