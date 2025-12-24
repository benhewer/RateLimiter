package rate.project.ratelimiter.registries;

import org.springframework.stereotype.Component;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to gather all the rate limiter implementations into a hashmap.
 * The getRateLimiter() function returns the correct class corresponding to the provided algorithm.
 */
@Component
public class RateLimiterRegistry {

  private final Map<RateLimiterAlgorithm, RateLimiter> registry;

  public RateLimiterRegistry(List<RateLimiter> rateLimiters) {
    registry = new HashMap<>();
    for (RateLimiter rateLimiter : rateLimiters) {
      registry.put(rateLimiter.getAlgorithm(), rateLimiter);
    }
  }

  public RateLimiter getRateLimiter(RateLimiterAlgorithm rateLimiterAlgorithm) {
    RateLimiter rateLimiter = registry.get(rateLimiterAlgorithm);
    if (rateLimiter == null) {
      throw new IllegalArgumentException("No such rate limiter class: " + rateLimiterAlgorithm);
    }
    return rateLimiter;
  }

}
