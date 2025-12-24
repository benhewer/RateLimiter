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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTests {

  @Mock
  private RuleRepository ruleRepository;
  @Mock
  private RuleMapper mapper;

  @InjectMocks
  private RuleService service;

  private final RuleDTO dto = new RuleDTO(
          "user:potassiumlover33:post",
          RateLimiterAlgorithm.LEAKY_BUCKET,
          new LeakyBucketParameters(10, 1)
  );

  private final RuleEntity entity = new RuleEntity(dto.key(), dto.algorithm(), dto.parameters());

  @Test
  void whenRuleDoesNotExist_thenCreateRuleShouldCallRuleRepositorySave() {
    // Mock the mapper and rule not being in db
    when(mapper.toEntity(dto)).thenReturn(entity);
    when(ruleRepository.existsById(entity.key())).thenReturn(false);

    boolean success = service.createRule(dto);
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
    when(mapper.toEntity(dto)).thenReturn(entity);
    when(ruleRepository.existsById(entity.key())).thenReturn(true);

    boolean success = service.createRule(dto);
    assertFalse(success);

    // Ensure ruleRepository.save() was not called
    verify(ruleRepository, never()).save(any(RuleEntity.class));
  }

  @Test
  void getRuleShouldCallRuleRepositoryFindById() {
    // Mock rule in db
    when(ruleRepository.findById(entity.key())).thenReturn(Optional.of(entity));
    when(mapper.toDTO(entity)).thenReturn(dto);

    RuleDTO result = service.getRule(entity.key());
    assertEquals(dto, result);

    verify(ruleRepository).findById(entity.key());
  }

  @Test
  void whenRuleExists_thenUpdateRuleShouldCallRuleRepositorySave() {
    // Mock mapper and rule being in db already
    when(mapper.toEntity(dto)).thenReturn(entity);
    when(ruleRepository.existsById(entity.key())).thenReturn(true);

    boolean success = service.updateRule(dto.key(), dto);
    assertTrue(success);

    // Ensure ruleRepository.save() was called once with the correct arguments
    ArgumentCaptor<RuleEntity> captor = ArgumentCaptor.forClass(RuleEntity.class);
    verify(ruleRepository).save(captor.capture());

    RuleEntity ruleEntity = captor.getValue();
    assertEquals(entity, ruleEntity);
  }

  @Test
  void whenRuleDoesNotExist_thenUpdateRuleShouldReturnFalse() {
    // Mock mapper and rule being in db already
    when(mapper.toEntity(dto)).thenReturn(entity);
    when(ruleRepository.existsById(entity.key())).thenReturn(false);

    boolean success = service.updateRule(dto.key(), dto);
    assertFalse(success);

    // Ensure ruleRepository.save() was not called
    verify(ruleRepository, never()).save(any(RuleEntity.class));
  }

  @Test
  void whenKeysAreNotConsistent_thenUpdateRuleShouldReturnFalse() {
    when(mapper.toEntity(dto)).thenReturn(entity);

    boolean success = service.updateRule("incorrect:key", dto);
    assertFalse(success);

    // Ensure ruleRepository.save() was not called
    verify(ruleRepository, never()).save(any(RuleEntity.class));
  }

  @Test
  void whenRuleDoesNotExist_thenDeleteRuleShouldReturnNull() {
    // Mock rule not in db
    when(ruleRepository.findById(entity.key())).thenReturn(Optional.empty());

    RuleDTO rule = service.deleteRule(entity.key());
    assertNull(rule);

    // Ensure ruleRepository.deleteById() was not called
    verify(ruleRepository, never()).deleteById(any(String.class));
  }

  @Test
  void whenRuleExists_thenDeleteRuleShouldCallRuleRepositoryDeleteById() {
    // Mock rule in db
    when(ruleRepository.findById(entity.key())).thenReturn(Optional.of(entity));
    when(mapper.toDTO(entity)).thenReturn(dto);

    RuleDTO rule = service.deleteRule(entity.key());
    assertEquals(dto, rule);

    // Ensure ruleRepository.deleteById was called once
    verify(ruleRepository).deleteById(entity.key());
  }

}
