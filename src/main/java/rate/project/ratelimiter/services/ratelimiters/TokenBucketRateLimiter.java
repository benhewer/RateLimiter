package rate.project.ratelimiter.services.ratelimiters;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

import java.util.List;

public final class TokenBucketRateLimiter implements RateLimiter {

  private final RedisTemplate<String, RateLimiterState> redis;
  private final RedisScript<@NotNull List<Long>> tokenBucketScript;

  private final long capacity;
  private final long refillRate;

  public TokenBucketRateLimiter(
          RedisTemplate<String, RateLimiterState> redis,
          RedisScript<@NotNull List<Long>> tokenBucketScript,
          TokenBucketParameters parameters
  ) {
    this.redis = redis;
    this.tokenBucketScript = tokenBucketScript;
    this.capacity = parameters.capacity();
    this.refillRate = parameters.refillRate();
  }

  @Override
  public CheckResponse tryAcquire(String key) {
    List<Long> result = redis.execute(
            tokenBucketScript,
            List.of(key),
            String.valueOf(capacity),
            String.valueOf(refillRate),
            String.valueOf(System.currentTimeMillis())
    );

    boolean allowed = result.get(0) == 1;
    long remaining = result.get(1);
    long retryAfterMillis = result.get(2);

    return new CheckResponse(allowed, remaining, retryAfterMillis);
  }

}
