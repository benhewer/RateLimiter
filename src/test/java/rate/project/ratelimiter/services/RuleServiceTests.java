package rate.project.ratelimiter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.mappers.RuleMapper;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTests {

  @Mock
  private RuleRepository ruleRepository;
  @Mock
  private RuleMapper mapper;

  @InjectMocks
  private RuleService service;

  @Test
  void createRuleShouldSaveRuleToDatabase() {
    RuleDTO dto = new RuleDTO(
            "user:potassiumlover33:post",
            RateLimiterAlgorithm.LEAKY_BUCKET,
            new LeakyBucketParameters(
                    10, 1
            )
    );

    RuleEntity entity = new RuleEntity(dto.key(), dto.algorithm(), dto.parameters());

    when(mapper.toEntity(dto)).thenReturn(entity);

    boolean initialized = service.createRule(dto);

    assertTrue(initialized);

    // Ensure ruleRepository.save() was called once with the correct arguments
    ArgumentCaptor<RuleEntity> captor = ArgumentCaptor.forClass(RuleEntity.class);
    verify(ruleRepository).save(captor.capture());

    RuleEntity ruleEntity = captor.getValue();
    assertEquals(dto.key(), ruleEntity.key());
    assertEquals(dto.algorithm(), ruleEntity.algorithm());
    assertEquals(dto.parameters(), ruleEntity.parameters());
  }

}
