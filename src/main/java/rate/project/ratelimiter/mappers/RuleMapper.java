package rate.project.ratelimiter.mappers;

import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.entities.mongo.RuleEntity;

/**
 * Maps between RuleDTO and RuleEntity.
 */
@Component
public class RuleMapper {

  public RuleEntity toEntity(RuleDTO dto, String projectId) {
    return new RuleEntity(null, projectId, dto.ruleKey(), dto.algorithm(), dto.parameters());
  }

  public RuleDTO toDTO(RuleEntity entity) {
    return new RuleDTO(entity.ruleKey(), entity.algorithm(), entity.parameters());
  }

}
