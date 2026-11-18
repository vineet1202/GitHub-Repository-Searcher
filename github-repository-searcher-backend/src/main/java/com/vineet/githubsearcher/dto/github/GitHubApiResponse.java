package com.vineet.githubsearcher.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GitHubApiResponse(
        @JsonProperty("total_count") Integer totalCount,
        @JsonProperty("incomplete_results") Boolean incompleteResults,
        List<GitHubRepositoryItem> items
) {
    public record GitHubRepositoryItem(
            Long id,
            String name,
            String description,
            GitHubOwner owner,
            String language,
            @JsonProperty("stargazers_count") Integer stars,
            @JsonProperty("forks_count") Integer forks,
            @JsonProperty("updated_at") Instant lastUpdated
    ) {
    }

    public record GitHubOwner(String login) {
    }
}
