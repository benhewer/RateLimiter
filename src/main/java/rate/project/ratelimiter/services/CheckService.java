package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.factories.RateLimiterFactory;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

@Service
public class CheckService {

  private final RateLimiterFactory rateLimiterFactory;

  public CheckService(RateLimiterFactory rateLimiterFactory) {
    this.rateLimiterFactory = rateLimiterFactory;
  }

  public CheckResponse checkAndUpdate(String projectId, String ruleKey, String userKey) {
    // Get the correct implementation of RateLimiter based on the rule's configured algorithm
    RateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(projectId, ruleKey);
    if (rateLimiter == null) {
      return null;
    }

    return rateLimiter.tryAcquire(userKey);
  }

}
