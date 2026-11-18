package com.vineet.githubsearcher.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GitHubSearchRequest(
        @NotBlank(message = "Query is required")
        @Size(max = 200, message = "Query must not exceed 200 characters")
        String query,

        @Size(max = 100, message = "Language must not exceed 100 characters")
        String language,

        String sort,

        @Min(value = 1, message = "Page must be at least 1")
        @Max(value = 100, message = "Page must not exceed 100")
        Integer page,

        @Min(value = 1, message = "Size must be at least 1")
        @Max(value = 100, message = "Size must not exceed 100")
        Integer size
) {
    public int pageOrDefault() { return page == null ? 1 : page; }
    public int sizeOrDefault() { return size == null ? 20 : size; }
}
