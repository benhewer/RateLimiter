package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.dtos.RuleDTO;
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

  public RuleService(RuleRepository ruleRepository, RuleMapper mapper) {
    this.ruleRepository = ruleRepository;
    this.mapper = mapper;
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

    ruleRepository.save(rule);
    return true;
  }

  public RuleDTO deleteRule(String key) {
    RuleEntity rule = ruleRepository.findById(key).orElse(null);
    if (rule == null) {
      return null;
    }

    ruleRepository.deleteById(key);
    return mapper.toDTO(rule);
  }

}
