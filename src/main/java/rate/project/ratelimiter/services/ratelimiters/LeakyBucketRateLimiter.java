package rate.project.ratelimiter.services.ratelimiters;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

import java.util.List;

public final class LeakyBucketRateLimiter implements RateLimiter {

  private final RedisOperations<String, RateLimiterState> redis;
  private final RedisScript<@NotNull List<Long>> leakyBucketScript;

  private final long capacity;
  private final long outflowRate;

  public LeakyBucketRateLimiter(
          RedisOperations<String, RateLimiterState> redis,
          RedisScript<@NotNull List<Long>> leakyBucketScript,
          LeakyBucketParameters parameters
  ) {
    this.redis = redis;
    this.leakyBucketScript = leakyBucketScript;
    this.capacity = parameters.capacity();
    this.outflowRate = parameters.outflowRate();
  }

  @Override
  public CheckResponse tryAcquire(String key) {
    List<Long> result = redis.execute(
            leakyBucketScript,
            List.of(key),
            String.valueOf(capacity),
            String.valueOf(outflowRate),
            String.valueOf(System.currentTimeMillis())
    );

    boolean allowed = result.get(0) == 1;
    long remaining = result.get(1);
    long retryAfterMillis = result.get(2);

    return new CheckResponse(allowed, remaining, retryAfterMillis);
  }

}
