package com.admissionconsultancy.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(
            @Value("${spring.application.name:admissionconsultancy}") String appName,
            Environment env) {

        String[] activeProfiles = env.getActiveProfiles();

        String currentEnvironment = activeProfiles.length > 0
                ? activeProfiles[0]
                : env.getDefaultProfiles()[0];

        return registry -> registry.config()
                .commonTags(
                        "application", appName,
                        "env", currentEnvironment
                );
    }
}