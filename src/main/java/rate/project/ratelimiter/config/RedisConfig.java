package rate.project.ratelimiter.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, RateLimiterState> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, RateLimiterState> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(new GenericToStringSerializer<@NotNull RateLimiterState>(RateLimiterState.class));

    return template;
  }

}
