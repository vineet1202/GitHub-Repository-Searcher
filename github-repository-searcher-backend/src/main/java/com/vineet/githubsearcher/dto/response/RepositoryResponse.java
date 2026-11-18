package com.vineet.githubsearcher.dto.response;

import com.vineet.githubsearcher.entity.GitHubRepository;

import java.time.Instant;

public record RepositoryResponse(
        Long id,
        String name,
        String description,
        String owner,
        String language,
        Integer stars,
        Integer forks,
        Instant lastUpdated
) {
    public static RepositoryResponse from(GitHubRepository repository) {
        return new RepositoryResponse(
                repository.getGithubId(),
                repository.getName(),
                repository.getDescription(),
                repository.getOwner(),
                repository.getLanguage(),
                repository.getStars(),
                repository.getForks(),
                repository.getLastUpdated()
        );
    }
}
