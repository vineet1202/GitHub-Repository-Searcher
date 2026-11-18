package com.vineet.githubsearcher.repository;

import com.vineet.githubsearcher.entity.GitHubRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GitHubRepositoryRepository extends JpaRepository<GitHubRepository, Long>,
        JpaSpecificationExecutor<GitHubRepository> {

    Optional<GitHubRepository> findByGithubId(Long githubId);

    List<GitHubRepository> findAllByGithubIdIn(Collection<Long> githubIds);
}
