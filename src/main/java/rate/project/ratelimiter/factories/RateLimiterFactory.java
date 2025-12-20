package rate.project.ratelimiter.factories;

import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.services.ratelimiters.LeakyBucketRateLimiter;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;
import rate.project.ratelimiter.services.ratelimiters.TokenBucketRateLimiter;

/**
 * Creates the appropriate rate limiter based on the given rule's algorithm.
 */
@Component
public final class RateLimiterFactory {

  public RateLimiter create(RuleEntity rule) {
    return switch (rule.algorithm()) {
      case TOKEN_BUCKET -> {
        TokenBucketParameters parameters = (TokenBucketParameters) rule.parameters();
        yield new TokenBucketRateLimiter(parameters);
      }
      case LEAKY_BUCKET -> {
        LeakyBucketParameters parameters = (LeakyBucketParameters) rule.parameters();
        yield new LeakyBucketRateLimiter(parameters);
      }
    };
  }

}
