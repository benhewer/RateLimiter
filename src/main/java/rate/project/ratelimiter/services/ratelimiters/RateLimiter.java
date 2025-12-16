package rate.project.ratelimiter.services.ratelimiters;

import org.springframework.stereotype.Component;

@Component
public interface RateLimiter {

  void initialize();

  boolean tryAcquire();

}
