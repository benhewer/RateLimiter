package rate.project.ratelimiter.entities.redis;

import org.springframework.data.redis.core.RedisHash;

/**
 * Maps directly to a redis entry. Stores only the temporary state of a leaky bucket.
 */
@RedisHash("leaky_bucket_state")
public class LeakyBucketState {

  private String key;
  private long tokens;
  private long lastRefillTime;

  public LeakyBucketState(String key, long tokens, long lastRefillTime) {
    this.key = key;
    this.tokens = tokens;
    this.lastRefillTime = lastRefillTime;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getTokens() {
    return tokens;
  }

  public void setTokens(long tokens) {
    this.tokens = tokens;
  }

  public long getLastRefillTime() {
    return lastRefillTime;
  }

  public void setLastRefillTime(long lastRefillTime) {
    this.lastRefillTime = lastRefillTime;
  }

}