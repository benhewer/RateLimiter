package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.registries.RateLimiterRegistry;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

@Service
public class CheckService {

  private final RateLimiterRegistry rateLimiterRegistry;
  private final RuleRepository ruleRepository;

  public CheckService(RateLimiterRegistry rateLimiterRegistry, RuleRepository ruleRepository) {
    this.rateLimiterRegistry = rateLimiterRegistry;
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
    RateLimiter rateLimiter = rateLimiterRegistry.getRateLimiter(rule.algorithm());

    return rateLimiter.tryAcquire(key, rule.parameters());
  }

}
