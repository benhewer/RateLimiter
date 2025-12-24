package rate.project.ratelimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import rate.project.ratelimiter.listeners.CacheInvalidator;

/**
 * Used to clear cache in all server nodes across a distributed system when a rule is updated.
 * This means stale rules aren't kept in cache.
 */
@Configuration
public class RedisPubSubConfig {

  @Bean
  public MessageListenerAdapter messageListenerAdapter(CacheInvalidator receiver) {
    return new MessageListenerAdapter(receiver, "cacheEvictListener");
  }

  @Bean
  public RedisMessageListenerContainer container(
          RedisConnectionFactory redisConnectionFactory,
          MessageListenerAdapter listenerAdapter
  ) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory);
    container.addMessageListener(listenerAdapter, new PatternTopic("rate-limiter-invalidation"));
    return container;
  }

}
