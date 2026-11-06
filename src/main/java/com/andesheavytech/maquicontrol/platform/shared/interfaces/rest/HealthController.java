package com.andesheavytech.maquicontrol.platform.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Liveness endpoint used to confirm the application boots and to back the container health check.
 */
@RestController
@Tag(name = "Health", description = "Liveness check")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Check that the platform is up")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
