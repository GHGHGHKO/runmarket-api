package com.runmarket.api.adapter.out.email;

import com.runmarket.api.domain.event.EmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailAdapter emailAdapter;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailVerification(EmailVerificationEvent event) {
        emailAdapter.sendVerificationEmail(event.email(), event.verificationLink());
    }
}
