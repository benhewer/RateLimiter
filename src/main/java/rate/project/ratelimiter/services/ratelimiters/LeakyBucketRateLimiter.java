package rate.project.ratelimiter.services.ratelimiters;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.factories.RedisScriptFactory;

@Component
public final class LeakyBucketRateLimiter implements RateLimiter {

  private final RedisOperations<String, String> redis;
  private final RedisScriptFactory redisScriptFactory;

  public LeakyBucketRateLimiter(
          RedisOperations<String, String> redis,
          RedisScriptFactory rescriptFactory
  ) {
    this.redis = redis;
    this.redisScriptFactory = rescriptFactory;
  }

  @Override
  public RateLimiterAlgorithm getAlgorithm() {
    return RateLimiterAlgorithm.LEAKY_BUCKET;
  }

  @Override
  public CheckResponse tryAcquire(String key, AlgorithmParameters parameters) {
    // TODO: Run Lua script
    return new CheckResponse(false, 0, 0);
  }

}
