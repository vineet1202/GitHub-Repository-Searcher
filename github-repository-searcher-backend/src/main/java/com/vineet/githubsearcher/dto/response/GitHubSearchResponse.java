package com.vineet.githubsearcher.dto.response;

import java.util.List;

public record GitHubSearchResponse(
        String message,
        List<RepositoryResponse> repositories,
        int totalCount
) {
}
