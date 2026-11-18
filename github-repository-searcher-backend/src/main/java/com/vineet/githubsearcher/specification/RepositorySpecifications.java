package com.vineet.githubsearcher.specification;

import com.vineet.githubsearcher.entity.GitHubRepository;
import org.springframework.data.jpa.domain.Specification;

public final class RepositorySpecifications {

    private RepositorySpecifications() {
    }

    public static Specification<GitHubRepository> hasLanguage(String language) {
        return (root, query, builder) -> language == null || language.isBlank()
                ? builder.conjunction()
                : builder.equal(builder.lower(root.get("language")), language.toLowerCase());
    }

    public static Specification<GitHubRepository> hasMinimumStars(Integer minStars) {
        return (root, query, builder) -> minStars == null
                ? builder.conjunction()
                : builder.greaterThanOrEqualTo(root.get("stars"), minStars);
    }
}
