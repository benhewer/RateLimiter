package rate.project.ratelimiter.listeners;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidator {

  private final CacheManager cacheManager;

  public CacheInvalidator(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  // Listens for a signal sent by the redisTemplate to clear cache
  @SuppressWarnings("unused")
  public void cacheEvictListener(String key) {
    Cache cache = cacheManager.getCache("RateLimiterCache");
    if (cache != null) {
      cache.evict(key);
    }
  }

}
