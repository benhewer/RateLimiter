package rate.project.ratelimiter.entities.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * Maps directly to a redis entry. Stores only the temporary state of a rate limiter algorithm.
 */
@RedisHash("rate_limiter_state")
public class RateLimiterState implements Serializable {

  @Id
  private String key;
  private long level;
  private long lastUpdateTime;

  public RateLimiterState(String key, long level, long lastUpdateTime) {
    this.key = key;
    this.level = level;
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getLevel() {
    return level;
  }

  public void setLevel(long tokens) {
    this.level = tokens;
  }

  public long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RateLimiterState that = (RateLimiterState) o;
    return key.equals(that.key);
  }

}