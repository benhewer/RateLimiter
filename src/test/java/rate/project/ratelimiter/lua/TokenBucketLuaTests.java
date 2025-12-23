package rate.project.ratelimiter.lua;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.factories.RedisScriptFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestRedisConfig.class)
public class TokenBucketLuaTests {

  @Autowired
  private RedisScriptFactory redisScriptFactory;

  @Autowired
  private RedisTemplate<String, RateLimiterState> testRedisTemplate;

  @Test
  void whenTokenAvailable_thenScriptShouldCorrectlyUpdateState() {
    List<Long> result = testRedisTemplate.execute(
            redisScriptFactory.tokenBucketScript(),
            List.of("user:potassiumlover33:login"),
            String.valueOf(10),
            String.valueOf(1),
            String.valueOf(System.currentTimeMillis())
    );

    assertNotNull(result);
    assertEquals(1L, result.get(0));
    assertEquals(9L, result.get(1));
    assertEquals(0L, result.get(2));
  }

  @Test
  void whenTokenNotAvailable_thenScriptShouldCorrectlyUpdateState() {
    // After executing script twice with capacity 1, the allowed bool should be false

    testRedisTemplate.execute(
            redisScriptFactory.tokenBucketScript(),
            List.of("user:potassiumlover33:post"),
            String.valueOf(1),
            String.valueOf(1),
            String.valueOf(System.currentTimeMillis())
    );

    List<Long> result = testRedisTemplate.execute(
            redisScriptFactory.tokenBucketScript(),
            List.of("user:potassiumlover33:post"),
            String.valueOf(1),
            String.valueOf(1),
            String.valueOf(System.currentTimeMillis())
    );

    assertNotNull(result);
    assertEquals(0L, result.get(0));
    assertEquals(0L, result.get(1));
    assertTrue(result.get(2) > 0);
  }

}
