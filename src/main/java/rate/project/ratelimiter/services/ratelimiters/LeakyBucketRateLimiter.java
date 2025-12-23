package rate.project.ratelimiter.services.ratelimiters;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.RateLimiterResponse;
import rate.project.ratelimiter.factories.RedisScriptFactory;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

@Component
public final class LeakyBucketRateLimiter implements RateLimiter {

  private final RedisOperations<String, String> redis;
  private final RedisScriptFactory redisScriptFactory;
  private final RuleRepository ruleRepository;

  public LeakyBucketRateLimiter(
          RedisOperations<String, String> redis,
          RedisScriptFactory rescriptFactory,
          RuleRepository ruleRepository
  ) {
    this.redis = redis;
    this.redisScriptFactory = rescriptFactory;
    this.ruleRepository = ruleRepository;
  }

  @Override
  public RateLimiterResponse tryAcquire(String key) {
    // TODO: Run Lua script
    return new RateLimiterResponse(false, 0, 0);
  }

}
