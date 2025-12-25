package rate.project.ratelimiter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.mappers.RuleMapper;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTests {

  @Mock
  private RuleRepository ruleRepository;
  @Mock
  private RuleMapper mapper;
  @Mock
  private RedisTemplate<String, RateLimiterState> redisTemplate;

  @InjectMocks
  private RuleService service;

  private final String projectId = "example";
  private final String ruleKey = "login";

  private final RuleDTO dto = new RuleDTO(
          ruleKey,
          RateLimiterAlgorithm.LEAKY_BUCKET,
          new LeakyBucketParameters(10, 1)
  );

  private final RuleEntity entity
          = new RuleEntity(null, projectId, dto.ruleKey(), dto.algorithm(), dto.parameters());

  @Test
  void whenRuleDoesNotExist_thenCreateRuleShouldCallRuleRepositorySave() {
    // Mock the mapper and rule not being in db
    when(mapper.toEntity(dto, projectId)).thenReturn(entity);
    when(ruleRepository.existsByProjectIdAndRuleKey(projectId, ruleKey)).thenReturn(false);

    boolean success = service.createRule(projectId, dto);
    assertTrue(success);

    // Ensure ruleRepository.save() was called once with the correct arguments
    ArgumentCaptor<RuleEntity> captor = ArgumentCaptor.forClass(RuleEntity.class);
    verify(ruleRepository).save(captor.capture());

    RuleEntity ruleEntity = captor.getValue();
    assertEquals(entity, ruleEntity);
  }

  @Test
  void whenRuleExists_thenCreateRuleShouldReturnFalse() {
    // Mock mapper and rule being in db already
    when(mapper.toEntity(dto, projectId)).thenReturn(entity);
    when(ruleRepository.existsByProjectIdAndRuleKey(projectId, ruleKey)).thenReturn(true);

    boolean success = service.createRule(projectId, dto);
    assertFalse(success);

    // Ensure ruleRepository.save() was not called
    verify(ruleRepository, never()).save(any(RuleEntity.class));
  }

  @Test
  void getRuleShouldCallRuleRepositoryFindById() {
    // Mock rule in db
    when(ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey)).thenReturn(Optional.of(entity));
    when(mapper.toDTO(entity)).thenReturn(dto);

    RuleDTO result = service.getRule(projectId, ruleKey);
    assertEquals(dto, result);

    verify(ruleRepository).findByProjectIdAndRuleKey(projectId, ruleKey);
  }

  @Test
  void whenRuleExists_thenUpdateRuleShouldCallRuleRepositorySave() {
    // Mock mapper and rule being in db already
    when(ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey)).thenReturn(Optional.of(entity));

    boolean success = service.updateRule(projectId, ruleKey, dto);
    assertTrue(success);

    // Ensure ruleRepository.save() was called once with the correct arguments
    ArgumentCaptor<RuleEntity> captor = ArgumentCaptor.forClass(RuleEntity.class);
    verify(ruleRepository).save(captor.capture());

    RuleEntity ruleEntity = captor.getValue();
    assertEquals(entity, ruleEntity);

    verify(redisTemplate).convertAndSend("rate-limiter-invalidation", projectId + ":" + ruleKey);
  }

  @Test
  void whenRuleDoesNotExist_thenUpdateRuleShouldReturnFalse() {
    // Mock rule not being in db
    when(ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey)).thenReturn(Optional.empty());

    boolean success = service.updateRule(projectId, ruleKey, dto);
    assertFalse(success);

    // Ensure ruleRepository.save() was not called
    verify(ruleRepository, never()).save(any(RuleEntity.class));
  }

  @Test
  void whenKeysAreNotConsistent_thenUpdateRuleShouldReturnFalse() {
    boolean success = service.updateRule(projectId, "incorrectKey", dto);
    assertFalse(success);

    // Ensure ruleRepository.save() was not called
    verify(ruleRepository, never()).save(any(RuleEntity.class));
  }

  @Test
  void whenRuleDoesNotExist_thenDeleteRuleShouldReturnNull() {
    // Mock rule not in db
    when(ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey)).thenReturn(Optional.empty());

    RuleDTO rule = service.deleteRule(projectId, ruleKey);
    assertNull(rule);

    // Ensure ruleRepository.deleteById() was not called
    verify(ruleRepository, never()).deleteById(any(String.class));
  }

  @Test
  void whenRuleExists_thenDeleteRuleShouldCallRuleRepositoryDeleteById() {
    // Mock rule in db
    when(ruleRepository.findByProjectIdAndRuleKey(projectId, ruleKey)).thenReturn(Optional.of(entity));
    when(mapper.toDTO(entity)).thenReturn(dto);

    RuleDTO rule = service.deleteRule(projectId, ruleKey);
    assertEquals(dto, rule);

    // Ensure ruleRepository.deleteById was called once
    verify(ruleRepository).deleteByProjectIdAndRuleKey(projectId, ruleKey);

    verify(redisTemplate).convertAndSend("rate-limiter-invalidation", projectId + ":" + ruleKey);
  }

}
