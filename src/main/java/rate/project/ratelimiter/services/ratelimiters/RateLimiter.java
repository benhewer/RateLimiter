package rate.project.ratelimiter.services.ratelimiters;

import rate.project.ratelimiter.dtos.CheckDTO;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;

/**
 * Represents a rate limiter.
 * tryAcquire() is used when POST /check is called.
 */
public interface RateLimiter {

  RateLimiterAlgorithm getAlgorithm();

  CheckDTO tryAcquire(String key, AlgorithmParameters parameters);

}
