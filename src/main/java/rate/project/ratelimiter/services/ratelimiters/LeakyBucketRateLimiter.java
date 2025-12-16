package rate.project.ratelimiter.services.ratelimiters;

import rate.project.ratelimiter.services.LeakyBucketService;

public final class LeakyBucketRateLimiter implements RateLimiter {

  private final String key;
  private final long capacity;
  private final long outflowRate;
  private final LeakyBucketService leakyBucketService;

  public LeakyBucketRateLimiter(String key, long capacity, long outflowRate, LeakyBucketService leakyBucketService) {
    this.key = key;
    this.capacity = capacity;
    this.outflowRate = outflowRate;
    this.leakyBucketService = leakyBucketService;
  }

  @Override
  public boolean tryAcquire() {
    return leakyBucketService.tryAddWater(key, capacity, outflowRate);
  }

}
