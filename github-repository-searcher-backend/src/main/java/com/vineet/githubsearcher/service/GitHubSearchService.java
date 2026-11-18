package com.vineet.githubsearcher.service;

import com.vineet.githubsearcher.client.GitHubClient;
import com.vineet.githubsearcher.dto.github.GitHubApiResponse;
import com.vineet.githubsearcher.dto.request.GitHubSearchRequest;
import com.vineet.githubsearcher.dto.response.GitHubSearchResponse;
import com.vineet.githubsearcher.dto.response.RepositoryResponse;
import com.vineet.githubsearcher.entity.GitHubRepository;
import com.vineet.githubsearcher.repository.GitHubRepositoryRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class GitHubSearchService {

    private final GitHubClient gitHubClient;
    private final GitHubRepositoryRepository repository;

    @Transactional
    public GitHubSearchResponse search(GitHubSearchRequest request) {
        String sort = RepositorySort.githubSort(request.sort());
        String query = buildQuery(request.query(), request.language());

        GitHubApiResponse response = gitHubClient.searchRepositories(
                query, sort, request.pageOrDefault(), request.sizeOrDefault());

        List<GitHubRepository> incoming = response.items().stream()
                .filter(item -> item.id() != null && item.name() != null
                        && item.owner() != null && item.owner().login() != null)
                .map(this::toEntity)
                .toList();
        
        if (incoming.isEmpty()) {
            return new GitHubSearchResponse(
                    "No valid repositories returned by GitHub",
                    List.of(),
                    response.totalCount() == null ? 0 : response.totalCount()
            );
        }
        
        List<Long> incomingIds = new ArrayList<>();
        for (GitHubRepository repo : incoming) {
            incomingIds.add(repo.getGithubId());
        }
        
        List<GitHubRepository> existingRepos = repository.findAllByGithubIdIn(incomingIds);
        
        Map<Long, GitHubRepository> existing = new HashMap<>();
        for (GitHubRepository repo : existingRepos) {
            existing.put(repo.getGithubId(), repo);
        }

        List<GitHubRepository> toSave = new ArrayList<>(incoming.size());
        for (GitHubRepository item : incoming) {
            GitHubRepository stored = existing.get(item.getGithubId());
            if (stored != null) {
                stored.updateFrom(item);
                toSave.add(stored);
            } else {
                toSave.add(item);
            }
        }

        List<GitHubRepository> saved = repository.saveAll(toSave);

        List<RepositoryResponse> results = saved.stream()
                .map(RepositoryResponse::from)
                .toList();

        return new GitHubSearchResponse(
                "Repositories fetched and saved successfully",
                results,
                response.totalCount() == null ? results.size() : response.totalCount()
        );
    }

    private String buildQuery(String query, String language) {
        String normalizedQuery = query.trim();
        if (language == null || language.isBlank()) {
            return normalizedQuery;
        }
        return normalizedQuery + " language:" + language.trim();
    }

    private GitHubRepository toEntity(GitHubApiResponse.GitHubRepositoryItem item) {
        return new GitHubRepository(
                item.id(),
                item.name(),
                item.description(),
                item.owner().login(),
                item.language(),
                item.stars() == null ? 0 : item.stars(),
                item.forks() == null ? 0 : item.forks(),
                item.lastUpdated() == null ? java.time.Instant.EPOCH : item.lastUpdated()
        );
    }
}
