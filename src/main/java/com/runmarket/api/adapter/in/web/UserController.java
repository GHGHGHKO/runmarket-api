package com.runmarket.api.adapter.in.web;

import com.runmarket.api.common.SecurityUtils;
import com.runmarket.api.domain.port.in.user.WithdrawUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final WithdrawUseCase withdrawUseCase;

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw() {
        withdrawUseCase.withdraw(SecurityUtils.currentUserEmail());
        return ResponseEntity.noContent().build();
    }
}
