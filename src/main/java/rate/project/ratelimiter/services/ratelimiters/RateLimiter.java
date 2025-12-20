package rate.project.ratelimiter.services.ratelimiters;

/**
 * Represents a rate limiter.
 * tryAcquire() is used when POST /check is called.
 */
public interface RateLimiter {

  boolean tryAcquire(String key);

}
