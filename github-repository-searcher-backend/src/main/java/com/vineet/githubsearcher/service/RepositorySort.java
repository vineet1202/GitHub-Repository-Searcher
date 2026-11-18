package com.vineet.githubsearcher.service;

import org.springframework.data.domain.Sort;

import java.util.Locale;

public final class RepositorySort {

    private RepositorySort() {
    }

    public static Sort from(String sortBy) {
        String property = switch (sortBy == null ? "stars" : sortBy.toLowerCase(Locale.ROOT)) {
            case "stars" -> "stars";
            case "forks" -> "forks";
            case "updated" -> "lastUpdated";
            default -> throw new IllegalArgumentException(
                    "Invalid sort value. Allowed values: stars, forks, updated");
        };
        return Sort.by(Sort.Direction.DESC, property);
    }

    public static String githubSort(String sortBy) {
        String value = sortBy == null ? "stars" : sortBy.toLowerCase(Locale.ROOT);
        if (!value.equals("stars") && !value.equals("forks") && !value.equals("updated")) {
            throw new IllegalArgumentException(
                    "Invalid sort value. Allowed values: stars, forks, updated");
        }
        return value;
    }
}
