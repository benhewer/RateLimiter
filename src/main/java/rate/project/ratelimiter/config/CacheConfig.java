package rate.project.ratelimiter.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

  @Bean
  public Caffeine<@NotNull Object, @NotNull Object> caffeine() {
    return Caffeine.newBuilder()
            .expireAfterWrite(300, TimeUnit.SECONDS)
            .initialCapacity(10);
  }

  @Bean
  public CacheManager cacheManager(Caffeine<@NotNull Object, @NotNull Object> caffeine) {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }

}
