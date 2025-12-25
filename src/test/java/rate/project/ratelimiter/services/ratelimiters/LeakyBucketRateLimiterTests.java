package rate.project.ratelimiter.services.ratelimiters;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeakyBucketRateLimiterTests {

  @Mock
  private RedisTemplate<String, RateLimiterState> redisTemplate;

  @Mock
  private RedisScript<@NotNull List<Long>> leakyBucketScript;

  private LeakyBucketRateLimiter rateLimiter;

  private final LeakyBucketParameters parameters = new LeakyBucketParameters(10, 1);

  private final String name = "example:login";

  @BeforeEach
  void setUp() {
    rateLimiter = new LeakyBucketRateLimiter(
            redisTemplate,
            leakyBucketScript,
            parameters,
            name
    );
  }

  @Test
  void tryAcquireShouldReturnRateLimiterResponse() {
    List<Long> redisResult = List.of(1L, 9L, 0L);

    String userKey = "potassiumlover33";
    when(redisTemplate.execute(
            eq(leakyBucketScript),
            eq(List.of(name + ":" + userKey)),
            eq(String.valueOf(parameters.capacity())),
            eq(String.valueOf(parameters.outflowRate())),
            anyString()
    )).thenReturn(redisResult);

    CheckResponse response = rateLimiter.tryAcquire(userKey);
    assertEquals(new CheckResponse(true, 9, 0), response);
  }

}