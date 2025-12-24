package rate.project.ratelimiter.factories;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;
import rate.project.ratelimiter.services.ratelimiters.LeakyBucketRateLimiter;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;
import rate.project.ratelimiter.services.ratelimiters.TokenBucketRateLimiter;

@Service
public class RateLimiterFactory {

  private final RuleRepository ruleRepository;
  private final RedisTemplate<String, RateLimiterState> redis;
  private final RedisScriptFactory scripts;

  public RateLimiterFactory(
          RuleRepository ruleRepository,
          RedisTemplate<String, RateLimiterState> redis,
          RedisScriptFactory scripts
  ) {
    this.ruleRepository = ruleRepository;
    this.redis = redis;
    this.scripts = scripts;
  }

  public RateLimiter getRateLimiter(String key) {
    RuleEntity rule = ruleRepository.findById(key).orElse(null);
    if (rule == null) {
      return null;
    }

    return switch (rule.algorithm()) {
      case TOKEN_BUCKET -> new TokenBucketRateLimiter(redis, scripts.tokenBucketScript(), (TokenBucketParameters) rule.parameters());
      case LEAKY_BUCKET -> new LeakyBucketRateLimiter(redis, scripts.leakyBucketScript(), (LeakyBucketParameters) rule.parameters());
    };
  }
}
