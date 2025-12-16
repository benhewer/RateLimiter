package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;

@Service
public class TokenBucketService {

  public void fillBucket(String key, long capacity) {

  }

  public boolean tryUseToken(String key, long capacity, long refillRate) {
    return false;
  }

}
