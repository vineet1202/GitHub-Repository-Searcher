package com.vineet.githubsearcher.service;

import com.vineet.githubsearcher.client.GitHubClient;
import com.vineet.githubsearcher.dto.github.GitHubApiResponse;
import com.vineet.githubsearcher.dto.request.GitHubSearchRequest;
import com.vineet.githubsearcher.entity.GitHubRepository;
import com.vineet.githubsearcher.repository.GitHubRepositoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class GitHubSearchServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private GitHubRepositoryRepository repository;

    @InjectMocks
    private GitHubSearchService searchService;

    @Test
    void shouldInsertNewRepositoryWhenItDoesNotExist() {

        // Arrange
        GitHubApiResponse.GitHubRepositoryItem item =
                new GitHubApiResponse.GitHubRepositoryItem(
                        101L,
                        "spring-boot",
                        "Spring Boot Framework",
                        new GitHubApiResponse.GitHubOwner("spring-projects"),
                        "Java",
                        500,
                        200,
                        Instant.parse("2026-11-18T10:00:00Z")
                );

        GitHubApiResponse response = new GitHubApiResponse(
                1,
                false,
                List.of(item)
        );

        GitHubSearchRequest request = new GitHubSearchRequest(
                "spring boot", "Java", "stars", 1, 20
        );

        when(gitHubClient.searchRepositories(
                "spring boot language:Java", "stars", 1, 20
        )).thenReturn(response);

        // Repository does not exist in database
        when(repository.findAllByGithubIdIn(anyCollection()))
                .thenReturn(List.of());

        // Return the same entities passed to saveAll()
        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        searchService.search(request);

        // Assert
        ArgumentCaptor<List<GitHubRepository>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(repository).saveAll(captor.capture());

        List<GitHubRepository> savedRepositories = captor.getValue();

        assertEquals(1, savedRepositories.size());

        GitHubRepository saved = savedRepositories.get(0);

        assertEquals(101L, saved.getGithubId());
        assertEquals("spring-boot", saved.getName());
        assertEquals("Spring Boot Framework", saved.getDescription());
        assertEquals("spring-projects", saved.getOwner());
        assertEquals("Java", saved.getLanguage());
        assertEquals(500, saved.getStars());
        assertEquals(200, saved.getForks());

        verify(repository).findAllByGithubIdIn(anyCollection());
    }

    @Test
    void shouldUpdateExistingRepositoryInsteadOfInsertingDuplicate() {

        // Arrange
        GitHubRepository existingRepository = new GitHubRepository(
                101L,
                "spring-boot",
                "Old description",
                "old-owner",
                "Java",
                100,
                50,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        GitHubApiResponse.GitHubRepositoryItem updatedItem =
                new GitHubApiResponse.GitHubRepositoryItem(
                        101L,
                        "spring-boot",
                        "Updated description",
                        new GitHubApiResponse.GitHubOwner("spring-projects"),
                        "Java",
                        500,
                        200,
                        Instant.parse("2026-11-18T10:00:00Z")
                );

        GitHubApiResponse response = new GitHubApiResponse(
                1,
                false,
                List.of(updatedItem)
        );

        GitHubSearchRequest request = new GitHubSearchRequest(
                "spring boot", "Java", "stars", 1, 20
        );

        when(gitHubClient.searchRepositories(
                "spring boot language:Java", "stars", 1, 20
        )).thenReturn(response);

        // Existing repository is returned from database
        when(repository.findAllByGithubIdIn(anyCollection()))
                .thenReturn(List.of(existingRepository));

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        searchService.search(request);

        // Assert
        ArgumentCaptor<List<GitHubRepository>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(repository).saveAll(captor.capture());

        List<GitHubRepository> savedRepositories = captor.getValue();

        assertEquals(1, savedRepositories.size());

        GitHubRepository saved = savedRepositories.get(0);

        // Critical assertion: same entity instance was reused
        assertSame(existingRepository, saved);

        // Verify updated values
        assertEquals(101L, saved.getGithubId());
        assertEquals("Updated description", saved.getDescription());
        assertEquals("spring-projects", saved.getOwner());
        assertEquals(500, saved.getStars());
        assertEquals(200, saved.getForks());

        verify(repository).findAllByGithubIdIn(anyCollection());
    }
    
    
    @Test
    void shouldUpdateExistingAndInsertNewRepositoriesInSameSearch() {

        // Arrange
        GitHubRepository existingRepository = new GitHubRepository(
                101L, "spring-boot", "Old description", "spring",
                "Java", 100, 50, Instant.parse("2026-01-01T10:00:00Z")
        );

        GitHubApiResponse.GitHubRepositoryItem existingItem =
                new GitHubApiResponse.GitHubRepositoryItem(
                        101L, "spring-boot", "Updated description",
                        new GitHubApiResponse.GitHubOwner("spring-projects"),
                        "Java", 500, 200,
                        Instant.parse("2026-11-18T10:00:00Z")
                );

        GitHubApiResponse.GitHubRepositoryItem newItem =
                new GitHubApiResponse.GitHubRepositoryItem(
                        202L, "hibernate", "ORM framework",
                        new GitHubApiResponse.GitHubOwner("hibernate"),
                        "Java", 300, 100,
                        Instant.parse("2026-11-18T10:00:00Z")
                );

        GitHubApiResponse response = new GitHubApiResponse(
                2, false, List.of(existingItem, newItem)
        );

        GitHubSearchRequest request =
                new GitHubSearchRequest("java", null, "stars", 1, 20);

        when(gitHubClient.searchRepositories("java", "stars", 1, 20))
                .thenReturn(response);

        when(repository.findAllByGithubIdIn(anyCollection()))
                .thenReturn(List.of(existingRepository));

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        searchService.search(request);

        // Assert
        ArgumentCaptor<List<GitHubRepository>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(repository).saveAll(captor.capture());

        List<GitHubRepository> saved = captor.getValue();

        assertEquals(2, saved.size());

        // Existing repository was updated
        assertSame(existingRepository, saved.get(0));
        assertEquals("Updated description", saved.get(0).getDescription());
        assertEquals(500, saved.get(0).getStars());

        // New repository was inserted
        assertEquals(202L, saved.get(1).getGithubId());
        assertEquals("hibernate", saved.get(1).getName());
        assertEquals(300, saved.get(1).getStars());
    }
    
    @Test
    void shouldSkipInvalidRepositoriesFromGitHubResponse() {

        // Arrange
        GitHubApiResponse.GitHubRepositoryItem validItem =
                new GitHubApiResponse.GitHubRepositoryItem(
                        101L, "spring-boot", "Framework",
                        new GitHubApiResponse.GitHubOwner("spring-projects"),
                        "Java", 500, 200, Instant.now()
                );

        GitHubApiResponse.GitHubRepositoryItem missingId =
                new GitHubApiResponse.GitHubRepositoryItem(
                        null, "invalid-repo", "Description",
                        new GitHubApiResponse.GitHubOwner("owner"),
                        "Java", 100, 50, Instant.now()
                );

        GitHubApiResponse.GitHubRepositoryItem missingName =
                new GitHubApiResponse.GitHubRepositoryItem(
                        102L, null, "Description",
                        new GitHubApiResponse.GitHubOwner("owner"),
                        "Java", 100, 50, Instant.now()
                );

        GitHubApiResponse.GitHubRepositoryItem missingOwner =
                new GitHubApiResponse.GitHubRepositoryItem(
                        103L, "invalid-owner", "Description",
                        null, "Java", 100, 50, Instant.now()
                );

        GitHubApiResponse.GitHubRepositoryItem missingOwnerLogin =
                new GitHubApiResponse.GitHubRepositoryItem(
                        104L, "invalid-login", "Description",
                        new GitHubApiResponse.GitHubOwner(null),
                        "Java", 100, 50, Instant.now()
                );

        GitHubApiResponse response = new GitHubApiResponse(
                5, false,
                List.of(validItem, missingId, missingName, missingOwner, missingOwnerLogin)
        );

        GitHubSearchRequest request =
                new GitHubSearchRequest("java", null, "stars", 1, 20);

        when(gitHubClient.searchRepositories("java", "stars", 1, 20))
                .thenReturn(response);

        when(repository.findAllByGithubIdIn(anyCollection()))
                .thenReturn(List.of());

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        searchService.search(request);

        // Assert
        ArgumentCaptor<List<GitHubRepository>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(repository).saveAll(captor.capture());

        List<GitHubRepository> saved = captor.getValue();

        assertEquals(1, saved.size());
        assertEquals(101L, saved.get(0).getGithubId());
    }
    
    @Test
    void shouldDefaultNullStarsAndForksToZero() {

        // Arrange
        GitHubApiResponse.GitHubRepositoryItem item =
                new GitHubApiResponse.GitHubRepositoryItem(
                        101L,
                        "spring-boot",
                        "Framework",
                        new GitHubApiResponse.GitHubOwner("spring-projects"),
                        "Java",
                        null,
                        null,
                        Instant.parse("2026-11-18T10:00:00Z")
                );

        GitHubApiResponse response =
                new GitHubApiResponse(1, false, List.of(item));

        GitHubSearchRequest request =
                new GitHubSearchRequest("java", null, "stars", 1, 20);

        when(gitHubClient.searchRepositories("java", "stars", 1, 20))
                .thenReturn(response);

        when(repository.findAllByGithubIdIn(anyCollection()))
                .thenReturn(List.of());

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        searchService.search(request);

        // Assert
        ArgumentCaptor<List<GitHubRepository>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(repository).saveAll(captor.capture());

        GitHubRepository saved = captor.getValue().get(0);

        assertEquals(0, saved.getStars());
        assertEquals(0, saved.getForks());
    }
}
