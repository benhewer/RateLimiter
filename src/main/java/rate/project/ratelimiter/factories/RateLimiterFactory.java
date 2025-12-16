package rate.project.ratelimiter.factories;

import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.services.LeakyBucketService;
import rate.project.ratelimiter.services.TokenBucketService;
import rate.project.ratelimiter.services.ratelimiters.LeakyBucketRateLimiter;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;
import rate.project.ratelimiter.services.ratelimiters.TokenBucketRateLimiter;

@Component
public final class RateLimiterFactory {

  private final TokenBucketService tokenBucketService;
  private final LeakyBucketService leakyBucketService;

  public RateLimiterFactory(TokenBucketService tokenBucketService, LeakyBucketService leakyBucketService) {
    this.tokenBucketService = tokenBucketService;
    this.leakyBucketService = leakyBucketService;
  }

  public RateLimiter create(RuleEntity rule) {
    return switch (rule.algorithm()) {
      case TOKEN_BUCKET -> {
        TokenBucketParameters parameters = (TokenBucketParameters) rule.parameters();
        yield new TokenBucketRateLimiter(
                rule.key(),
                parameters.capacity(),
                parameters.refillRate(),
                tokenBucketService
        );
      }
      case LEAKY_BUCKET -> {
        LeakyBucketParameters parameters = (LeakyBucketParameters) rule.parameters();
        yield new LeakyBucketRateLimiter(
                rule.key(),
                parameters.capacity(),
                parameters.outflowRate(),
                leakyBucketService
        );
      }
    };
  }

}
