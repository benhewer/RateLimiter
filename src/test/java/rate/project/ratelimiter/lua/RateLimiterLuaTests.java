package rate.project.ratelimiter.lua;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.factories.RedisScriptFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestRedisConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RateLimiterLuaTests {

  @Autowired
  private RedisScriptFactory redisScriptFactory;

  @Autowired
  private RedisTemplate<String, RateLimiterState> testRedisTemplate;

  // Provides all scripts to be tested with below methods
  private Stream<RedisScript<@NotNull List<Long>>> rateLimiterScripts() {
    return Stream.of(
            redisScriptFactory.tokenBucketScript(),
            redisScriptFactory.leakyBucketScript()
    );
  }

  @ParameterizedTest
  @MethodSource("rateLimiterScripts")
  void whenTokenAvailable_thenScriptShouldCorrectlyUpdateState(RedisScript<@NotNull List<Long>> rateLimiterScript) {
    List<Long> result = testRedisTemplate.execute(
            rateLimiterScript,
            List.of("example:post:potassiumlover33"),
            String.valueOf(10),
            String.valueOf(1),
            String.valueOf(System.currentTimeMillis())
    );

    assertNotNull(result);
    assertEquals(1L, result.get(0));
    assertEquals(9L, result.get(1));
    assertEquals(0L, result.get(2));
  }

  @ParameterizedTest
  @MethodSource("rateLimiterScripts")
  void whenTokenNotAvailable_thenScriptShouldCorrectlyUpdateState(RedisScript<@NotNull List<Long>> rateLimiterScript) {
    // After executing script twice with capacity 1, the allowed bool should be false

    testRedisTemplate.execute(
            rateLimiterScript,
            List.of("example:login:potassiumlover33"),
            String.valueOf(1),
            String.valueOf(1),
            String.valueOf(System.currentTimeMillis())
    );

    List<Long> result = testRedisTemplate.execute(
            rateLimiterScript,
            List.of("example:login:potassiumlover33"),
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
