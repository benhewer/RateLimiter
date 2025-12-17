package rate.project.ratelimiter.services;

import org.springframework.stereotype.Service;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.repositories.mongo.RuleEntityRepository;

/**
 * Provides an easy way to interact with the RuleEntity collection in MongoDB.
 */
@Service
public class RuleEntityService {

  private final RuleEntityRepository ruleEntityRepository;

  public RuleEntityService(RuleEntityRepository ruleEntityRepository) {
    this.ruleEntityRepository = ruleEntityRepository;
  }

  public void save(RuleEntity ruleEntity) {
    ruleEntityRepository.save(ruleEntity);
  }

  public RuleEntity getRule(String key) {
    return ruleEntityRepository.findById(key).orElse(null);
  }

  public boolean exists(String key) {
    return ruleEntityRepository.existsById(key);
  }

}
