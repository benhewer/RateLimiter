package rate.project.ratelimiter.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.mappers.RuleMapper;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

/**
 * Handles the logic behind the RuleController.
 */
@Service
public class RuleService {

  private final RuleRepository ruleRepository;
  private final RuleMapper mapper;
  private final RedisTemplate<String, RateLimiterState> redisTemplate;

  public RuleService(RuleRepository ruleRepository, RuleMapper mapper, RedisTemplate<String, RateLimiterState> redisTemplate) {
    this.ruleRepository = ruleRepository;
    this.mapper = mapper;
    this.redisTemplate = redisTemplate;
  }

  public boolean createRule(RuleDTO ruleDTO) {
    RuleEntity rule = mapper.toEntity(ruleDTO);

    // Check if the rule already exists
    if (ruleRepository.existsById(rule.key())) {
      return false;
    }

    // Save the rule to the database
    ruleRepository.save(rule);
    return true;
  }

  public RuleDTO getRule(String key) {
    System.out.println(key);
    RuleEntity rule = ruleRepository.findById(key).orElse(null);
    if (rule == null) {
      return null;
    }
    return mapper.toDTO(rule);
  }

  public boolean updateRule(String key, RuleDTO ruleDTO) {
    RuleEntity rule = mapper.toEntity(ruleDTO);

    // Ensure the keys are consistent
    if (!key.equals(rule.key())) {
      return false;
    }

    // Check if the rule doesn't exist yet
    if (!ruleRepository.existsById(key)) {
      return false;
    }

    // Clear from cache
    redisTemplate.convertAndSend("rate-limiter-invalidation", key);

    ruleRepository.save(rule);
    return true;
  }

  public RuleDTO deleteRule(String key) {
    RuleEntity rule = ruleRepository.findById(key).orElse(null);
    if (rule == null) {
      return null;
    }

    // Clear from cache
    redisTemplate.convertAndSend("rate-limiter-invalidation", key);

    ruleRepository.deleteById(key);
    return mapper.toDTO(rule);
  }

}
