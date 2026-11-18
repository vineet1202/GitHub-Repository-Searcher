package com.vineet.githubsearcher.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.vineet.githubsearcher.dto.github.GitHubApiResponse;
import com.vineet.githubsearcher.exception.GitHubApiException;

class GitHubClientTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitHubClient = new GitHubClient(restClient);
    }

    @SuppressWarnings("unchecked")
	@Test
    void shouldThrowTooManyRequestsWhenGitHubRateLimitIsExceeded() {

        // Arrange
        HttpClientErrorException forbiddenException =
                HttpClientErrorException.create(
                        HttpStatus.FORBIDDEN,
                        "Forbidden",
                        HttpHeaders.EMPTY,
                        "API rate limit exceeded".getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );

        when(restClient.get()
                .uri(any(Function.class))
                .retrieve()
                .body(GitHubApiResponse.class))
                .thenThrow(forbiddenException);

        // Act
        GitHubApiException exception = assertThrows(
                GitHubApiException.class,
                () -> gitHubClient.searchRepositories(
                        "spring boot language:Java",
                        "stars",
                        1,
                        20
                )
        );

        // Assert
        assertEquals(
                "GitHub API rate limit exceeded. Please try again later.",
                exception.getMessage()
        );
    }
}
