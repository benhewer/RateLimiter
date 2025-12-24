package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.factories.RateLimiterFactory;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

@Service
public class CheckService {

  private final RateLimiterFactory rateLimiterFactory;
  private final RuleRepository ruleRepository;

  public CheckService(RateLimiterFactory rateLimiterFactory, RuleRepository ruleRepository) {
    this.rateLimiterFactory = rateLimiterFactory;
    this.ruleRepository = ruleRepository;
  }

  public CheckResponse checkAndUpdate(String key) {
    System.out.println(key);
    RuleEntity rule = ruleRepository.findById(key).orElse(null);
    System.out.println(rule);
    if (rule == null) {
      return null;
    }

    // Get the correct implementation of RateLimiter based on the rule's configured algorithm
    RateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(key);
    if (rateLimiter == null) {
      return null;
    }

    return rateLimiter.tryAcquire(key);
  }

}
