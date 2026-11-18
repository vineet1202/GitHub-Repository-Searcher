package com.vineet.githubsearcher.service;

import com.vineet.githubsearcher.dto.response.RepositoryPageResponse;
import com.vineet.githubsearcher.dto.response.RepositoryResponse;
import com.vineet.githubsearcher.entity.GitHubRepository;
import com.vineet.githubsearcher.exception.ResourceNotFoundException;
import com.vineet.githubsearcher.repository.GitHubRepositoryRepository;
import com.vineet.githubsearcher.specification.RepositorySpecifications;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RepositoryQueryService {

    private final GitHubRepositoryRepository repository;

    @Transactional(readOnly = true)
    public RepositoryPageResponse findRepositories(
            String language, Integer minStars, String sort, int page, int size) {

        if (minStars != null && minStars < 0) {
            throw new IllegalArgumentException("minStars must be zero or greater");
        }
        if (page < 0) {
            throw new IllegalArgumentException("page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }

        Specification<GitHubRepository> specification = Specification.allOf(
                RepositorySpecifications.hasLanguage(language),
                RepositorySpecifications.hasMinimumStars(minStars)
        );

        Page<RepositoryResponse> result = repository.findAll(
                        specification,
                        PageRequest.of(page, size, RepositorySort.from(sort)))
                .map(RepositoryResponse::from);

        return RepositoryPageResponse.from(result);
    }
    
    @Transactional(readOnly = true)
    public RepositoryResponse findByGithubId(Long githubId) {
        GitHubRepository result = repository.findByGithubId(githubId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Repository not found: " + githubId));
        return RepositoryResponse.from(result);
    }

}
