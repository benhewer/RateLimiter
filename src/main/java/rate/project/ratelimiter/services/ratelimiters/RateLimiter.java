package rate.project.ratelimiter.services.ratelimiters;

import rate.project.ratelimiter.dtos.CheckResponse;

/**
 * Represents a rate limiter.
 * tryAcquire() is used when POST /check is called.
 */
public interface RateLimiter {

  CheckResponse tryAcquire(String userKey);

}
