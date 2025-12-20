package rate.project.ratelimiter.services.ratelimiters;

import rate.project.ratelimiter.dtos.RateLimiterResponse;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;

public final class TokenBucketRateLimiter implements RateLimiter {

  private final long capacity;
  private final long refillRate;

  public TokenBucketRateLimiter(TokenBucketParameters parameters) {
    this.capacity = parameters.capacity();
    this.refillRate = parameters.refillRate();
  }

  @Override
  public RateLimiterResponse tryAcquire(String key) {
    // TODO: Run lua script
    return new RateLimiterResponse(false, 0, 0);
  }

}
