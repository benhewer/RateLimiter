package rate.project.ratelimiter.dtos;

public record RateLimiterResponse(boolean allowed, long remaining, long retryAfterMillis) { }
