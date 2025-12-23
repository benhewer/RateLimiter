package rate.project.ratelimiter.services.ratelimiters;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.RateLimiterResponse;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.factories.RedisScriptFactory;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

import java.util.List;

@Component
public final class TokenBucketRateLimiter implements RateLimiter {

  private final RedisOperations<String, String> redis;
  private final RedisScriptFactory redisScriptFactory;
  private final RuleRepository ruleRepository;

  public TokenBucketRateLimiter(
          RedisOperations<String, String> redis,
          RedisScriptFactory redisScriptFactory,
          RuleRepository ruleRepository
  ) {
    this.redis = redis;
    this.redisScriptFactory = redisScriptFactory;
    this.ruleRepository = ruleRepository;
  }

  @Override
  public RateLimiterResponse tryAcquire(String key) {
    RuleEntity rule = ruleRepository.findById(key).orElse(null);
    if (rule == null) {
      return null;
    }

    TokenBucketParameters parameters = (TokenBucketParameters) rule.parameters();

    List<Long> result = redis.execute(
            redisScriptFactory.tokenBucketScript(),
            List.of(key),
            parameters.capacity(),
            parameters.refillRate(),
            System.currentTimeMillis()
    );

    boolean allowed = result.get(0) == 1;
    long remaining = result.get(1);
    long retryAfterMillis = result.get(2);

    return new RateLimiterResponse(allowed, remaining, retryAfterMillis);
  }

}
