package com.runmarket.pacer.eventbus.listener;

import com.runmarket.pacer.domain.event.EmailVerificationEvent;
import com.runmarket.pacer.domain.port.out.email.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailPort emailPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailVerification(EmailVerificationEvent event) {
        emailPort.sendVerificationEmail(event.email(), event.verificationLink());
    }
}
