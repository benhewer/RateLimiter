package rate.project.ratelimiter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import org.springframework.data.redis.connection.RedisConnection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestRedisConfig.class)
public class TokenBucketServiceIntegrationTests {

  @Autowired
  private RateLimiterStateService rateLimiterStateService;

  @Autowired
  private TokenBucketService tokenBucketService;

  @Autowired
  RedisTemplate<String, RateLimiterState> testRedisTemplate;

  @BeforeEach
  void clearRedis() {
    testRedisTemplate.execute((RedisConnection connection) -> {
      connection.serverCommands().flushAll();
      return null;
    });
  }

  @Test
  void redisShouldBeRealAndReachableAndInContainer() {
    String pong = testRedisTemplate.execute(
            RedisConnectionCommands::ping,
            true
    );

    assertEquals("PONG", pong);

    LettuceConnectionFactory factory = (LettuceConnectionFactory) testRedisTemplate.getConnectionFactory();
    assertNotNull(factory);

    // Ensure Redis is not running locally (on default port)
    assertNotEquals(6379, factory.getPort());
  }

  @Test
  void fillBucketShouldInitializeRedisState() {
    String key = "testBucket";
    long capacity = 10;

    tokenBucketService.fillBucket(key, capacity);

    RateLimiterState state = rateLimiterStateService.getState(key);
    assertNotNull(state);
    assertEquals(key, state.getKey());
    assertEquals(capacity, state.getLevel());
    assertTrue(state.getLastUpdateTime() > 0);
  }

}
