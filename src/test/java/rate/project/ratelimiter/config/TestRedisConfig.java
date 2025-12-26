package rate.project.ratelimiter.config;

import com.redis.testcontainers.RedisContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.testcontainers.utility.DockerImageName;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

@TestConfiguration
public class TestRedisConfig {

  public static final RedisContainer redisContainer =
          new RedisContainer(DockerImageName.parse("redis:latest"))
                  .withExposedPorts(6379);

  static {
    redisContainer.start();
  }

  // Create a new connection factory for testing, so all services use the test container redis connection
  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    LettuceConnectionFactory factory =
            new LettuceConnectionFactory(
                    redisContainer.getHost(),
                    redisContainer.getMappedPort(6379)
            );
    factory.afterPropertiesSet();
    return factory;
  }

  @Bean
  @Primary
  public RedisTemplate<String, RateLimiterState> testRedisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, RateLimiterState> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(new GenericToStringSerializer<@NotNull RateLimiterState>(RateLimiterState.class));

    return template;
  }

}
