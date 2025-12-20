package rate.project.ratelimiter.services.ratelimiters;

import rate.project.ratelimiter.dtos.RateLimiterResponse;

/**
 * Represents a rate limiter.
 * tryAcquire() is used when POST /check is called.
 */
public interface RateLimiter {

  RateLimiterResponse tryAcquire(String key);

}
