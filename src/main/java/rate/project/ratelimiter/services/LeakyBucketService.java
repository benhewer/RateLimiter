package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;

@Service
public class LeakyBucketService {

  public boolean tryAddWater(String key, long capacity, long outflowRate) {
    return false;
  }

}
