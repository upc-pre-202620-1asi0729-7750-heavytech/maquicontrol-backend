package com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables the scheduler that drives the {@code OutboxEventRelay}.
 */
@Configuration
@EnableScheduling
public class OutboxConfiguration {
}
