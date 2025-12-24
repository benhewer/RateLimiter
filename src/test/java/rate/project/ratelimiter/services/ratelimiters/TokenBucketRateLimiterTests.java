package rate.project.ratelimiter.services.ratelimiters;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.CheckDTO;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.factories.RedisScriptFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenBucketRateLimiterTests {

  @Mock
  private RedisScriptFactory scriptFactory;

  @Mock
  private RedisTemplate<String, RateLimiterState> redisTemplate;

  @InjectMocks
  private TokenBucketRateLimiter rateLimiter;

  private final TokenBucketParameters parameters = new TokenBucketParameters(10, 1);

  private final RuleEntity rule = new RuleEntity(
          "user:potassiumlover33:login",
          RateLimiterAlgorithm.TOKEN_BUCKET,
          parameters
  );

  @Test
  void getAlgorithmShouldReturnCorrectAlgorithm() {
    assertEquals(RateLimiterAlgorithm.TOKEN_BUCKET, rateLimiter.getAlgorithm());
  }

  @Test
  @SuppressWarnings("unchecked")
  void tryAcquireShouldReturnRateLimiterResponse() {
    List<Long> redisResult = List.of(1L, 9L, 0L);

    when(scriptFactory.tokenBucketScript()).thenReturn(mock(RedisScript.class));
    when(redisTemplate.execute(
            eq(scriptFactory.tokenBucketScript()),
            eq(List.of(rule.key())),
            eq(String.valueOf(parameters.capacity())),
            eq(String.valueOf(parameters.refillRate())),
            anyString()
    )).thenReturn(redisResult);

    CheckDTO response = rateLimiter.tryAcquire(rule.key(), parameters);
    assertEquals(new CheckDTO(true, 9, 0), response);
  }

}
