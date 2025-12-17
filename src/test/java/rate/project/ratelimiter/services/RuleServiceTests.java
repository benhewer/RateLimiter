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
import rate.project.ratelimiter.factories.RateLimiterFactory;
import rate.project.ratelimiter.mappers.RuleMapper;
import rate.project.ratelimiter.repositories.mongo.RuleEntityRepository;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTests {

  @Mock
  private RuleEntityRepository ruleEntityRepository;
  @Mock
  private RateLimiterFactory rateLimiterFactory;
  @Mock
  private RateLimiter rateLimiter;
  @Mock
  private RuleMapper mapper;

  @InjectMocks
  private RuleService service;

  @Test
  void createRuleShouldSaveRuleToDatabaseAndInitializeRateLimiterState() {
    RuleDTO rule = new RuleDTO(
            "user:potassiumlover33:post",
            RateLimiterAlgorithm.LEAKY_BUCKET,
            new LeakyBucketParameters(
                    10, 1
            )
    );

    RuleEntity fakeEntity = new RuleEntity(rule.key(), rule.algorithm(), rule.parameters());

    // Mock the mapper and factory behavior to isolate RuleService
    when(mapper.toEntity(rule)).thenReturn(fakeEntity);
    when(rateLimiterFactory.create(fakeEntity)).thenReturn(rateLimiter);
    when(rateLimiter.initialize()).thenReturn(true);

    boolean initialized = service.createRule(rule);

    assertTrue(initialized);

    // Ensure ruleRepository.save() was called once with the correct arguments
    ArgumentCaptor<RuleEntity> captor = ArgumentCaptor.forClass(RuleEntity.class);
    verify(ruleEntityRepository).save(captor.capture());

    RuleEntity ruleEntity = captor.getValue();
    assertEquals(rule.key(), ruleEntity.key());
    assertEquals(rule.algorithm(), ruleEntity.algorithm());

    // Ensure rateLimiterFactory.create() was called once
    verify(rateLimiterFactory).create(ruleEntity);

    // Ensure rateLimiter.initialize() was called once
    verify(rateLimiter).initialize();
  }

}
