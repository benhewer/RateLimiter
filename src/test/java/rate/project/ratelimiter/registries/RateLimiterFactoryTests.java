package rate.project.ratelimiter.registries;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.factories.RateLimiterFactory;
import rate.project.ratelimiter.factories.RedisScriptFactory;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;
import rate.project.ratelimiter.services.ratelimiters.LeakyBucketRateLimiter;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;
import rate.project.ratelimiter.services.ratelimiters.TokenBucketRateLimiter;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RateLimiterFactoryTests {

  @Mock
  private RuleRepository ruleRepository;

  @Mock
  private RedisScriptFactory redisScriptFactory;

  @Mock
  private RedisScript<@NotNull List<Long>> tokenBucketScript;

  @Mock
  private RedisScript<@NotNull List<Long>> leakyBucketScript;

  @InjectMocks
  private RateLimiterFactory rateLimiterFactory;

  private final RuleEntity tokenBucketEntity = new RuleEntity(
          "user:potassiumlover33:login",
          RateLimiterAlgorithm.TOKEN_BUCKET,
          new TokenBucketParameters(10, 1)
  );

  private final RuleEntity leakyBucketEntity = new RuleEntity(
          "user:potassiumlover33:post",
          RateLimiterAlgorithm.LEAKY_BUCKET,
          new LeakyBucketParameters(10, 1)
  );

  @Test
  void whenKeyNotFound_thenGetRateLimiterShouldReturnNull() {
    when(ruleRepository.findById(tokenBucketEntity.key())).thenReturn(Optional.empty());

    RateLimiter nullRateLimiter = rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    assertNull(nullRateLimiter);
  }

  @Test
  void whenKeyFound_thenGetRateLimiterShouldReturnCorrectRateLimiter() {
    // Token Bucket

    when(ruleRepository.findById(tokenBucketEntity.key())).thenReturn(Optional.of(tokenBucketEntity));
    when(redisScriptFactory.tokenBucketScript()).thenReturn(tokenBucketScript);

    RateLimiter tokenBucketRateLimiter = rateLimiterFactory.getRateLimiter(tokenBucketEntity.key());
    assertEquals(TokenBucketRateLimiter.class, tokenBucketRateLimiter.getClass());

    // Leaky Bucket

    when(ruleRepository.findById(leakyBucketEntity.key())).thenReturn(Optional.of(leakyBucketEntity));
    when(redisScriptFactory.leakyBucketScript()).thenReturn(leakyBucketScript);

    RateLimiter leakyBucketRateLimiter = rateLimiterFactory.getRateLimiter(leakyBucketEntity.key());
    assertEquals(LeakyBucketRateLimiter.class, leakyBucketRateLimiter.getClass());
  }

}
