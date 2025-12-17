package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

/**
 * Handles state for a token bucket rate limiter.
 */
@Service
public class TokenBucketService {

  private final RateLimiterStateService service;

  public TokenBucketService(RateLimiterStateService service) {
    this.service = service;
  }

  public boolean fillBucket(String key, long capacity) {
    RateLimiterState state = new RateLimiterState(key, capacity, System.currentTimeMillis());
    return service.initializeState(state);
  }

  public boolean tryUseToken(String key, long capacity, long refillRate) {
    return false;
  }

}
