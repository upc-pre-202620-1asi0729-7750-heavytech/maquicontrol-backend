package com.andesheavytech.maquicontrol.platform.iam.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.iam.application.commandservices.IdentityAccessCommandService;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SeedRolesCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Seeds the role catalog when the application starts.
 */
@Service
@Slf4j
public class ApplicationReadyEventHandler {

    private final IdentityAccessCommandService identityAccessCommandService;

    public ApplicationReadyEventHandler(IdentityAccessCommandService identityAccessCommandService) {
        this.identityAccessCommandService = identityAccessCommandService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        log.info("Verifying if roles seeding is needed");
        identityAccessCommandService.handle(new SeedRolesCommand());
        log.info("Roles seeding verified");
    }
}
