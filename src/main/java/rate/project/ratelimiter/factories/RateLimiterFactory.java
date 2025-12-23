package rate.project.ratelimiter.factories;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.services.ratelimiters.LeakyBucketRateLimiter;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;
import rate.project.ratelimiter.services.ratelimiters.TokenBucketRateLimiter;

import java.util.List;

/**
 * Creates the appropriate rate limiter based on the given rule's algorithm.
 * Handles the creation of the RedisScript objects.
 */
@Component
public final class RateLimiterFactory {

  private final RedisOperations<String, String> redis;

  private final RedisScript<@NotNull List<Long>> tokenBucketScript;
  private final RedisScript<@NotNull List<Long>> leakyBucketScript;

  public RateLimiterFactory(RedisOperations<String, String> redis) {
    this.redis = redis;

    this.tokenBucketScript = getRedisScript("scripts/token_bucket.lua");
    this.leakyBucketScript = getRedisScript("scripts/leaky_bucket.lua");
  }

  @SuppressWarnings("unchecked")
  private RedisScript<@NotNull List<Long>> getRedisScript(String filepath) {
    return RedisScript.of(
            new ClassPathResource(filepath),
            (Class<List<Long>>) (Class<?>) List.class
    );
  }

  public RateLimiter create(RuleEntity rule) {
    return switch (rule.algorithm()) {
      case TOKEN_BUCKET -> {
        TokenBucketParameters parameters = (TokenBucketParameters) rule.parameters();
        yield new TokenBucketRateLimiter(redis, tokenBucketScript, parameters);
      }
      case LEAKY_BUCKET -> {
        LeakyBucketParameters parameters = (LeakyBucketParameters) rule.parameters();
        yield new LeakyBucketRateLimiter(redis, leakyBucketScript, parameters);
      }
    };
  }

}
