package com.runmarket.pacer.application.service;

import com.runmarket.pacer.domain.port.in.user.WithdrawUseCase;
import com.runmarket.pacer.domain.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawService implements WithdrawUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void withdraw(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        userRepository.deleteById(user.getId());
        log.info("User withdrawn: email={}", email);
    }
}
