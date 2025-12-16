package rate.project.ratelimiter.services.ratelimiters;

import rate.project.ratelimiter.services.TokenBucketService;

public final class TokenBucketRateLimiter implements RateLimiter {

  private final String key;
  private final long capacity;
  private final long refillRate;
  private final TokenBucketService tokenBucketService;

  public TokenBucketRateLimiter(String key, long capacity, long refillRate, TokenBucketService tokenBucketService) {
    this.key = key;
    this.capacity = capacity;
    this.refillRate = refillRate;
    this.tokenBucketService = tokenBucketService;
  }

  @Override
  public boolean tryAcquire() {
    return tokenBucketService.tryUseToken(key, capacity, refillRate);
  }

}
