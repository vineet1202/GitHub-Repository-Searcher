package com.vineet.githubsearcher.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record RepositoryPageResponse(
        List<RepositoryResponse> repositories,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static RepositoryPageResponse from(Page<RepositoryResponse> result) {
        return new RepositoryPageResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
