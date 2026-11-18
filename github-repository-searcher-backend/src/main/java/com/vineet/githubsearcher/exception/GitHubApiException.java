package com.vineet.githubsearcher.exception;

import org.springframework.http.HttpStatus;

public class GitHubApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;
	private final HttpStatus status;

    public GitHubApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
