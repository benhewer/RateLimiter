package rate.project.ratelimiter.factories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import rate.project.ratelimiter.config.CacheConfig;
import rate.project.ratelimiter.config.TestMongoConfig;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;
import rate.project.ratelimiter.services.RuleService;
import rate.project.ratelimiter.services.ratelimiters.LeakyBucketRateLimiter;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;
import rate.project.ratelimiter.services.ratelimiters.TokenBucketRateLimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

// Spring boot is loaded to fully test the cache
@SpringBootTest
@Import({CacheConfig.class, TestMongoConfig.class})
public class RateLimiterFactoryTests {

  @MockitoSpyBean
  private RuleRepository ruleRepository;

  @Autowired
  private RuleService ruleService;

  @Autowired
  private RateLimiterFactory rateLimiterFactory;

  @Autowired
  private CacheManager cacheManager;

  private final RuleDTO tokenBucketEntity = new RuleDTO(
          "user:potassiumlover33:login",
          RateLimiterAlgorithm.TOKEN_BUCKET,
          new TokenBucketParameters(10, 1)
  );

  private final RuleDTO leakyBucketEntity = new RuleDTO(
          "user:potassiumlover33:post",
          RateLimiterAlgorithm.LEAKY_BUCKET,
          new LeakyBucketParameters(10, 1)
  );

  @BeforeEach
  void clearCache() {
    Cache cache = cacheManager.getCache("RateLimiterCache");
    if (cache != null) {
      cache.clear();
    }
  }

  @Test
  void whenKeyNotFound_thenGetRateLimiterShouldReturnNull() {
    RateLimiter nullRateLimiter = rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    assertNull(nullRateLimiter);
  }

  @Test
  void whenKeyFound_thenGetRateLimiterShouldReturnCorrectRateLimiter() {
    // Token Bucket
    ruleService.createRule(tokenBucketEntity);
    RateLimiter tokenBucketRateLimiter = rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    assertEquals(TokenBucketRateLimiter.class, tokenBucketRateLimiter.getClass());

    // Leaky Bucket
    ruleService.createRule(leakyBucketEntity);
    RateLimiter leakyBucketRateLimiter = rateLimiterFactory.getRateLimiter(leakyBucketEntity.key());
    assertEquals(LeakyBucketRateLimiter.class, leakyBucketRateLimiter.getClass());
  }

  @Test
  void shouldOnlyQueryMongoOnceForSameRule() {
    ruleService.createRule(tokenBucketEntity);

    rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());

    // Ensure only query DB once
    verify(ruleRepository).findById(anyString());
  }

  @Test
  void whenRuleIsDeleted_thenRuleShouldBeEvictedFromCache() {
    ruleService.createRule(tokenBucketEntity);
    rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    ruleService.deleteRule(tokenBucketEntity.key());

    RateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    assertNull(rateLimiter);
  }

  @Test
  void whenRuleIsUpdated_thenRuleShouldBeEvictedFromCache() {
    ruleService.createRule(tokenBucketEntity);
    rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    ruleService.updateRule(tokenBucketEntity.key(), tokenBucketEntity);

    rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());

    verify(ruleRepository, times(2)).findById(anyString());
  }

}
