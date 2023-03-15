package com.polarbookshop.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "polar")
public record ClientProperties(@NotNull URI catalogServiceUri) {}
