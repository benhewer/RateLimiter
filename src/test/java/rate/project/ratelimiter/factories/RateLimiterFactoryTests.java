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
import rate.project.ratelimiter.config.TestRedisConfig;
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
@Import({CacheConfig.class, TestMongoConfig.class, TestRedisConfig.class})
public class RateLimiterFactoryTests {

  @MockitoSpyBean
  private RuleRepository ruleRepository;

  @Autowired
  private RuleService ruleService;

  @Autowired
  private RateLimiterFactory rateLimiterFactory;

  @Autowired
  private CacheManager cacheManager;

  private final String projectId = "example";

  private final RuleDTO tokenBucketEntity = new RuleDTO(
          "login",
          RateLimiterAlgorithm.TOKEN_BUCKET,
          new TokenBucketParameters(10, 1)
  );

  private final RuleDTO leakyBucketEntity = new RuleDTO(
          "post",
          RateLimiterAlgorithm.LEAKY_BUCKET,
          new LeakyBucketParameters(10, 1)
  );

  @BeforeEach
  void setUp() {
    Cache cache = cacheManager.getCache("RateLimiterCache");
    if (cache != null) {
      cache.clear();
    }

    ruleRepository.deleteAll();
  }

  @Test
  void whenKeyNotFound_thenGetRateLimiterShouldReturnNull() {
    RateLimiter nullRateLimiter = rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());
    assertNull(nullRateLimiter);
  }

  @Test
  void whenKeyFound_thenGetRateLimiterShouldReturnCorrectRateLimiter() {
    // Token Bucket
    ruleService.createRule(projectId, tokenBucketEntity);
    RateLimiter tokenBucketRateLimiter = rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());
    assertEquals(TokenBucketRateLimiter.class, tokenBucketRateLimiter.getClass());

    // Leaky Bucket
    ruleService.createRule(projectId, leakyBucketEntity);
    RateLimiter leakyBucketRateLimiter = rateLimiterFactory.getRateLimiter(projectId, leakyBucketEntity.ruleKey());
    assertEquals(LeakyBucketRateLimiter.class, leakyBucketRateLimiter.getClass());
  }

  @Test
  void shouldOnlyQueryMongoOnceForSameRule() {
    ruleService.createRule(projectId, tokenBucketEntity);

    rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());
    rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());

    // Ensure only query DB once
    verify(ruleRepository).findByProjectIdAndRuleKey(projectId, tokenBucketEntity.ruleKey());
  }

  @Test
  void whenRuleIsDeleted_thenRuleShouldBeEvictedFromCache() {
    ruleService.createRule(projectId, tokenBucketEntity);
    rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());
    ruleService.deleteRule(projectId, tokenBucketEntity.ruleKey());

    RateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());
    assertNull(rateLimiter);
  }

  @Test
  void whenRuleIsUpdated_thenRuleShouldBeEvictedFromCache() {
    ruleService.createRule(projectId, tokenBucketEntity);
    rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());
    ruleService.updateRule(projectId, tokenBucketEntity.ruleKey(), tokenBucketEntity);

    rateLimiterFactory.getRateLimiter(projectId, tokenBucketEntity.ruleKey());

    // Called once in updateRule, and once in each getRateLimiter (as the cache should be flushed)
    verify(ruleRepository, times(3))
            .findByProjectIdAndRuleKey(projectId, tokenBucketEntity.ruleKey());
  }

}
