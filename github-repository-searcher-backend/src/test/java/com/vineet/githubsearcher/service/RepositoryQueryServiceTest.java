package com.vineet.githubsearcher.service;

import com.vineet.githubsearcher.exception.ResourceNotFoundException;
import com.vineet.githubsearcher.repository.GitHubRepositoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryQueryServiceTest {

    @Mock
    private GitHubRepositoryRepository repository;

    @InjectMocks
    private RepositoryQueryService queryService;

    @Test
    void shouldThrowExceptionWhenMinimumStarsIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> queryService.findRepositories("Java", -1, "stars", 0, 20)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowExceptionWhenPageIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> queryService.findRepositories("Java", 100, "stars", -1, 20)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowExceptionWhenSizeIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> queryService.findRepositories("Java", 100, "stars", 0, 0)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowExceptionWhenSizeExceeds100() {
        assertThrows(
                IllegalArgumentException.class,
                () -> queryService.findRepositories("Java", 100, "stars", 0, 101)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowExceptionWhenSortIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> queryService.findRepositories("Java", 100, "invalid", 0, 20)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRepositoryDoesNotExist() {
        Long githubId = 12345L;

        when(repository.findByGithubId(githubId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> queryService.findByGithubId(githubId)
        );

        verify(repository).findByGithubId(githubId);
    }
}
