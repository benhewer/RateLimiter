package rate.project.ratelimiter.services.ratelimiters;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.RateLimiterResponse;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;

import java.util.List;

public final class LeakyBucketRateLimiter implements RateLimiter {

  private final RedisOperations<String, String> redis;
  private final RedisScript<@NotNull List<Long>> leakyBucketScript;
  private final long capacity;
  private final long outflowRate;

  public LeakyBucketRateLimiter(
          RedisOperations<String, String> redis,
          RedisScript<@NotNull List<Long>> leakyBucketScript,
          LeakyBucketParameters parameters
  ) {
    this.redis = redis;
    this.leakyBucketScript = leakyBucketScript;
    this.capacity = parameters.capacity();
    this.outflowRate = parameters.outflowRate();
  }

  @Override
  public RateLimiterResponse tryAcquire(String key) {
    // TODO: Run Lua script
    return new RateLimiterResponse(false, 0, 0);
  }

}
