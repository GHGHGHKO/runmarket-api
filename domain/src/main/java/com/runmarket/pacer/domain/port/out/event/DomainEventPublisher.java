package com.runmarket.pacer.domain.port.out.event;

public interface DomainEventPublisher {
    void publish(Object event);
}
