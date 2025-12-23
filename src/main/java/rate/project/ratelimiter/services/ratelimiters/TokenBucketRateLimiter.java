package rate.project.ratelimiter.services.ratelimiters;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.RateLimiterResponse;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;

import java.util.List;

public final class TokenBucketRateLimiter implements RateLimiter {

  private final RedisOperations<String, String> redis;
  private final RedisScript<@NotNull List<Long>> tokenBucketScript;
  private final long capacity;
  private final long refillRate;

  public TokenBucketRateLimiter(
          RedisOperations<String, String> redis,
          RedisScript<@NotNull List<Long>> tokenBucketScript,
          TokenBucketParameters parameters
  ) {
    this.redis = redis;
    this.tokenBucketScript = tokenBucketScript;
    this.capacity = parameters.capacity();
    this.refillRate = parameters.refillRate();
  }

  @Override
  public RateLimiterResponse tryAcquire(String key) {
    List<Long> result = redis.execute(
            tokenBucketScript,
            List.of(key),
            capacity,
            refillRate,
            System.currentTimeMillis()
    );

    boolean allowed = result.get(0) == 1;
    long remaining = result.get(1);
    long retryAfterMillis = result.get(2);

    return new RateLimiterResponse(allowed, remaining, retryAfterMillis);
  }

}
