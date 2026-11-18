package com.vineet.githubsearcher.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(
        name = "github_repositories",
        indexes = {
                @Index(name = "idx_github_repo_language", columnList = "language"),
                @Index(name = "idx_github_repo_stars", columnList = "stars"),
                @Index(name = "idx_github_repo_forks", columnList = "forks"),
                @Index(name = "idx_github_repo_updated", columnList = "last_updated")
        }
)
@Getter
public class GitHubRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "github_id", nullable = false, unique = true)
    private Long githubId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String owner;

    private String language;

    @Column(nullable = false)
    private Integer stars;

    @Column(nullable = false)
    private Integer forks;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected GitHubRepository() {
    }

    public GitHubRepository(Long githubId, String name, String description, String owner,
                            String language, Integer stars, Integer forks, Instant lastUpdated) {
        this.githubId = githubId;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.language = language;
        this.stars = stars;
        this.forks = forks;
        this.lastUpdated = lastUpdated;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void updateFrom(GitHubRepository source) {
        this.name = source.name;
        this.description = source.description;
        this.owner = source.owner;
        this.language = source.language;
        this.stars = source.stars;
        this.forks = source.forks;
        this.lastUpdated = source.lastUpdated;
    }


}
