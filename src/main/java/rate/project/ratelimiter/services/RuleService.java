package rate.project.ratelimiter.services;

import org.springframework.cache.annotation.CacheEvict;
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

  public RuleService(
          RuleRepository ruleRepository,
          RuleMapper mapper,
          RedisTemplate<String, RateLimiterState> redisTemplate
  ) {
    this.ruleRepository = ruleRepository;
    this.mapper = mapper;
    this.redisTemplate = redisTemplate;
  }

  public boolean createRule(String projectId, RuleDTO ruleDTO) {
    RuleEntity rule = mapper.toEntity(ruleDTO, projectId);

    // Check if the rule already exists
    if (ruleRepository.existsByProjectIdAndRuleKey(rule.projectId(), rule.ruleKey())) {
      return false;
    }

    // Save the rule to the database
    ruleRepository.save(rule);
    return true;
  }

  public RuleDTO getRule(String projectId, String ruleKey) {
    RuleEntity rule = ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey).orElse(null);
    if (rule == null) {
      return null;
    }
    return mapper.toDTO(rule);
  }

  @CacheEvict(value = "RateLimiterCache", key = "#projectId + ':' + #ruleKey")
  public boolean updateRule(String projectId, String ruleKey, RuleDTO ruleDTO) {
    // Ensure the keys are consistent
    if (!ruleDTO.ruleKey().equals(ruleKey)) {
      return false;
    }

    RuleEntity rule = ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey).orElse(null);
    if (rule == null) {
      return false;
    }

    // New rule created with updated variables and original id
    RuleEntity updatedRule = new RuleEntity(
            rule.id(),
            projectId,
            ruleDTO.ruleKey(),
            ruleDTO.algorithm(),
            ruleDTO.parameters()
    );

    // Clear from cache (for distributed systems)
    redisTemplate.convertAndSend("rate-limiter-invalidation", projectId + ":" + ruleKey);

    ruleRepository.save(updatedRule);
    return true;
  }

  @CacheEvict(value = "RateLimiterCache", key = "#projectId + ':' + #ruleKey")
  public RuleDTO deleteRule(String projectId, String ruleKey) {
    RuleEntity rule = ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey).orElse(null);
    if (rule == null) {
      return null;
    }

    // Clear from cache (for distributed systems)
    redisTemplate.convertAndSend("rate-limiter-invalidation", projectId + ":" + rule.ruleKey());

    ruleRepository.deleteByProjectIdAndRuleKey(projectId, ruleKey);
    return mapper.toDTO(rule);
  }

}
