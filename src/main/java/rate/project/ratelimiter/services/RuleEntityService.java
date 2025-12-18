package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

/**
 * Provides an easy way to interact with the RuleEntity collection in MongoDB.
 */
@Service
public class RuleEntityService {

  private final RuleRepository ruleRepository;

  public RuleEntityService(RuleRepository ruleRepository) {
    this.ruleRepository = ruleRepository;
  }

  public void save(RuleEntity ruleEntity) {
    ruleRepository.save(ruleEntity);
  }

  public RuleEntity getRule(String key) {
    return ruleRepository.findById(key).orElse(null);
  }

  public boolean exists(String key) {
    return ruleRepository.existsById(key);
  }

}
