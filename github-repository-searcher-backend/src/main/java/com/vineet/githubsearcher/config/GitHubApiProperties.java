package com.vineet.githubsearcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "github.api")
public record GitHubApiProperties(
        String baseUrl,
        String token,
        Duration connectTimeout,
        Duration readTimeout
) {
}
