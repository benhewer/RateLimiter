package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.factories.RateLimiterFactory;
import rate.project.ratelimiter.mappers.RuleMapper;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

@Service
public class RuleService {

  private final RuleEntityService ruleEntityService;
  private final RuleMapper mapper;
  private final RateLimiterFactory rateLimiterFactory;

  public RuleService(RuleEntityService ruleEntityService, RuleMapper mapper, RateLimiterFactory rateLimiterFactory) {
    this.ruleEntityService = ruleEntityService;
    this.mapper = mapper;
    this.rateLimiterFactory = rateLimiterFactory;
  }

  public boolean createRule(RuleDTO ruleDTO) {
    RuleEntity rule = mapper.toEntity(ruleDTO);

    // Save the rule to the database
    ruleEntityService.save(rule);

    // Initialize the rate limiter algorithm in redis
    RateLimiter rateLimiter = rateLimiterFactory.create(rule);
    return rateLimiter.initialize();
  }

}
