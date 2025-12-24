package rate.project.ratelimiter.services.ratelimiters;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.CheckDTO;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.factories.RedisScriptFactory;

import java.util.List;

@Component
public final class TokenBucketRateLimiter implements RateLimiter {

  private final RedisTemplate<String, RateLimiterState> redis;
  private final RedisScriptFactory redisScriptFactory;

  public TokenBucketRateLimiter(
          RedisTemplate<String, RateLimiterState> redis,
          RedisScriptFactory redisScriptFactory
  ) {
    this.redis = redis;
    this.redisScriptFactory = redisScriptFactory;
  }

  @Override
  public RateLimiterAlgorithm getAlgorithm() {
    return RateLimiterAlgorithm.TOKEN_BUCKET;
  }

  @Override
  public CheckDTO tryAcquire(String key, AlgorithmParameters parameters) {
    TokenBucketParameters params = (TokenBucketParameters) parameters;
    List<Long> result = redis.execute(
            redisScriptFactory.tokenBucketScript(),
            List.of(key),
            String.valueOf(params.capacity()),
            String.valueOf(params.refillRate()),
            String.valueOf(System.currentTimeMillis())
    );

    boolean allowed = result.get(0) == 1;
    long remaining = result.get(1);
    long retryAfterMillis = result.get(2);

    return new CheckDTO(allowed, remaining, retryAfterMillis);
  }

}
