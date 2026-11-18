package com.vineet.githubsearcher.controller;

import com.vineet.githubsearcher.dto.request.GitHubSearchRequest;
import com.vineet.githubsearcher.dto.response.GitHubSearchResponse;
import com.vineet.githubsearcher.dto.response.RepositoryPageResponse;
import com.vineet.githubsearcher.service.GitHubSearchService;
import com.vineet.githubsearcher.service.RepositoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
@Validated
@AllArgsConstructor
@Tag(
        name = "GitHub Repository Search",
        description = "Search GitHub repositories and retrieve saved repositories"
)
public class GitHubController {

    private final GitHubSearchService searchService;
    private final RepositoryQueryService queryService;

    @PostMapping("/search")
    @Operation(
            summary = "Search GitHub repositories",
            description = "Searches repositories using the GitHub API, saves the results to PostgreSQL, and returns the fetched repositories."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repositories fetched and saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search request"),
            @ApiResponse(responseCode = "429", description = "GitHub API rate limit exceeded"),
            @ApiResponse(responseCode = "502", description = "GitHub API failure")
    })
    public ResponseEntity<GitHubSearchResponse> search(
            @Valid @RequestBody GitHubSearchRequest request) {

        GitHubSearchResponse response = searchService.search(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/repositories")
    @Operation(
            summary = "Get saved repositories",
            description = "Retrieves repositories stored in PostgreSQL with optional language and minimum-star filters, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repositories retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter, sort, or pagination parameter")
    })
    public ResponseEntity<RepositoryPageResponse> getRepositories(

            @Parameter(description = "Filter by programming language", example = "Java")
            @RequestParam(required = false) String language,

            @Parameter(description = "Minimum number of stars", example = "100")
            @RequestParam(required = false) @Min(0) Integer minStars,

            @Parameter(description = "Sort field: stars, forks, or updated", example = "stars")
            @RequestParam(defaultValue = "stars") String sort,

            @Parameter(description = "Zero-based page number", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,

            @Parameter(description = "Number of results per page (1-100)", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {

    	RepositoryPageResponse response =
                queryService.findRepositories(language, minStars, sort, page, size);

        return ResponseEntity.ok(response);
    }
}
