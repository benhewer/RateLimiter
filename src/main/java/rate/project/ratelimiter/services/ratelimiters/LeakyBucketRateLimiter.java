package rate.project.ratelimiter.services.ratelimiters;

import rate.project.ratelimiter.dtos.RateLimiterResponse;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;

public final class LeakyBucketRateLimiter implements RateLimiter {

  private final long capacity;
  private final long outflowRate;

  public LeakyBucketRateLimiter(LeakyBucketParameters parameters) {
    this.capacity = parameters.capacity();
    this.outflowRate = parameters.outflowRate();
  }

  @Override
  public RateLimiterResponse tryAcquire(String key) {
    // TODO: Run Lua script
    return new RateLimiterResponse(false, 0, 0);
  }

}
