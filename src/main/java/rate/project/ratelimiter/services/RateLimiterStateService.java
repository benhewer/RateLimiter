package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.repositories.redis.RateLimiterStateRepository;

@Service
public class RateLimiterStateService {

  private final RateLimiterStateRepository stateRepository;

  public RateLimiterStateService(RateLimiterStateRepository stateRepository) {
    this.stateRepository = stateRepository;
  }

  public boolean initializeState(RateLimiterState state) {
    if (!exists(state.getKey())) {
      stateRepository.save(state);
      return true;
    }
    System.out.println("State already exists");
    return false;
  }

  public RateLimiterState getState(String key) {
    return stateRepository.findById(key).orElse(null);
  }

  public boolean exists(String key) {
    return stateRepository.existsById(key);
  }

}
