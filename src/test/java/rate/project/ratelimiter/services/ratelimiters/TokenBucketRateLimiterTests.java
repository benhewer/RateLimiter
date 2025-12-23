package rate.project.ratelimiter.services.ratelimiters;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.RateLimiterResponse;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.factories.RedisScriptFactory;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenBucketRateLimiterTests {

  @Mock
  private RedisScriptFactory scriptFactory;

  @Mock
  private RedisOperations<String, String> redis;

  @Mock
  private RuleRepository ruleRepository;

  @InjectMocks
  private TokenBucketRateLimiter rateLimiter;

  @Test
  @SuppressWarnings("unchecked")
  void whenRuleInDB_thenTryAcquireShouldReturnRateLimiterResponse() {
    RuleEntity ruleEntity = new RuleEntity(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1));

    List<Long> redisResult = List.of(1L, 9L, 0L);

    // Mock the rule in db
    when(ruleRepository.findById(ruleEntity.key())).thenReturn(Optional.of(ruleEntity));
    when(scriptFactory.tokenBucketScript()).thenReturn(mock(RedisScript.class));
    when(redis.execute(
            eq(scriptFactory.tokenBucketScript()),
            eq(List.of(ruleEntity.key())),
            eq(10),
            eq(1),
            anyLong()
    )).thenReturn(redisResult);

    RateLimiterResponse response = rateLimiter.tryAcquire("user:potassiumlover33:login");
    assertEquals(new RateLimiterResponse(true, 9, 0),  response);
  }

  @Test
  void whenRuleNotInDB_thenTryAcquireShouldReturnNull() {
    RuleEntity ruleEntity = new RuleEntity(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1));

    // Mock the rule not in db
    when(ruleRepository.findById(ruleEntity.key())).thenReturn(Optional.empty());

    RateLimiterResponse response = rateLimiter.tryAcquire("user:potassiumlover33:login");
    assertNull(response);
  }

}
