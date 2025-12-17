package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

@Service
public class LeakyBucketService {

  private final RateLimiterStateService service;

  public LeakyBucketService(RateLimiterStateService service) {
    this.service = service;
  }

  public boolean emptyBucket(String key) {
    RateLimiterState state = new RateLimiterState(key, 0, System.currentTimeMillis());
    return service.initializeState(state);
  }

  public boolean tryAddWater(String key, long capacity, long outflowRate) {
    return false;
  }

}
