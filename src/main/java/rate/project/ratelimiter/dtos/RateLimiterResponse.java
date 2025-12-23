package rate.project.ratelimiter.dtos;

/**
 * Represents the response from a rate limiter.
 * This information will be returned to the client on POST /check.
 * @param allowed Whether the request is allowed or not.
 * @param remaining The number of requests remaining in the current window.
 * @param retryAfterMillis The number of milliseconds until the next window begins.
 */
public record RateLimiterResponse(boolean allowed, long remaining, long retryAfterMillis) { }
