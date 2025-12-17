package rate.project.ratelimiter.services.ratelimiters;

import org.springframework.stereotype.Component;

/**
 * Represents a rate limiter.
 * initialize() is used when POST /rule is called.
 * tryAcquire() is used when POST /check is called.
 */
@Component
public interface RateLimiter {

  boolean initialize();

  boolean tryAcquire();

}
