package com.vineet.githubsearcher.client;

import com.vineet.githubsearcher.dto.github.GitHubApiResponse;
import com.vineet.githubsearcher.exception.GitHubApiException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Component
@AllArgsConstructor
public class GitHubClient {

    private final RestClient restClient;

    public GitHubApiResponse searchRepositories(
            String query, String sort, int page, int size) {
        try {
            GitHubApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/repositories")
                            .queryParam("q", query)
                            .queryParam("sort", sort)
                            .queryParam("order", "desc")
                            .queryParam("page", page)
                            .queryParam("per_page", size)
                            .build())
                    .retrieve()
                    .body(GitHubApiResponse.class);

            if (response == null || response.items() == null) {
                throw new GitHubApiException(HttpStatus.BAD_GATEWAY,
                        "GitHub returned an invalid response");
            }
            return response;
        } catch (HttpStatusCodeException exception) {
            HttpStatus status = switch (exception.getStatusCode().value()) {
                case 403 -> HttpStatus.TOO_MANY_REQUESTS;
                case 404 -> HttpStatus.NOT_FOUND;
                case 422 -> HttpStatus.BAD_REQUEST;
                default -> HttpStatus.BAD_GATEWAY;
            };

            String message = status == HttpStatus.TOO_MANY_REQUESTS
                    ? "GitHub API rate limit exceeded. Please try again later."
                    : "GitHub API request failed with status " + exception.getStatusCode().value();

            throw new GitHubApiException(status, message);
        }
    }
}
