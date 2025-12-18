package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.repositories.redis.RateLimiterStateRepository;

/**
 * Provides an easy way to interact with the RateLimiterState collection in Redis.
 */
@Service
public class RateLimiterStateService {

  private final RateLimiterStateRepository rateLimiterStateRepository;

  public RateLimiterStateService(RateLimiterStateRepository rateLimiterStateRepository) {
    this.rateLimiterStateRepository = rateLimiterStateRepository;
  }

  public boolean initializeState(RateLimiterState state) {
    if (!exists(state.getKey())) {
      rateLimiterStateRepository.save(state);
      return true;
    }
    System.out.println("State already exists");
    return false;
  }

  public RateLimiterState getState(String key) {
    return rateLimiterStateRepository.findById(key).orElse(null);
  }

  public boolean exists(String key) {
    return rateLimiterStateRepository.existsById(key);
  }

}
