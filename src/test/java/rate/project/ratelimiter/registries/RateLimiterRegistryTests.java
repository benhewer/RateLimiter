package rate.project.ratelimiter.registries;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.services.ratelimiters.LeakyBucketRateLimiter;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;
import rate.project.ratelimiter.services.ratelimiters.TokenBucketRateLimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestRedisConfig.class) // Needed for complete Spring Boot set up
public class RateLimiterRegistryTests {

  @Autowired
  private RateLimiterRegistry rateLimiterRegistry;

  @Test
  void getRateLimiterShouldReturnCorrectRateLimiter() {
    RateLimiter tokenBucketRateLimiter = rateLimiterRegistry.getRateLimiter(RateLimiterAlgorithm.TOKEN_BUCKET);
    assertEquals(TokenBucketRateLimiter.class, tokenBucketRateLimiter.getClass());

    RateLimiter leakyBucketRateLimiter = rateLimiterRegistry.getRateLimiter(RateLimiterAlgorithm.LEAKY_BUCKET);
    assertEquals(LeakyBucketRateLimiter.class, leakyBucketRateLimiter.getClass());
  }

}
