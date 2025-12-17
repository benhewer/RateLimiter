package rate.project.ratelimiter.mappers;

import org.springframework.stereotype.Component;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.entities.mongo.RuleEntity;

/**
 * Maps between RuleDTO and RuleEntity.
 */
@Component
public class RuleMapper {

  public RuleEntity toEntity(RuleDTO dto) {
    return new RuleEntity(dto.key(), dto.algorithm(), dto.parameters());
  }

  public RuleDTO toDTO(RuleEntity entity) {
    return new RuleDTO(entity.key(), entity.algorithm(), entity.parameters());
  }

}
