package rate.project.ratelimiter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rate.project.ratelimiter.dtos.CheckDTO;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.registries.RateLimiterRegistry;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckServiceTests {

  @Mock
  private RateLimiterRegistry rateLimiterRegistry;

  @Mock
  private RuleRepository ruleRepository;

  @Mock
  private RateLimiter rateLimiter;

  @InjectMocks
  private CheckService checkService;

  private final RuleDTO dto = new RuleDTO(
          "user:potassiumlover33:login",
          RateLimiterAlgorithm.TOKEN_BUCKET,
          new TokenBucketParameters(10, 1)
  );

  private final RuleEntity entity = new RuleEntity(dto.key(), dto.algorithm(), dto.parameters());

  private final CheckDTO check = new CheckDTO(true, 9, 0);

  @Test
  void whenKeyNotInDB_thenCheckAndUpdateShouldReturnNull() {
    when(ruleRepository.findById(entity.key())).thenReturn(Optional.empty());

    CheckDTO rule = checkService.checkAndUpdate(entity.key());
    assertNull(rule);

    verify(rateLimiterRegistry, never()).getRateLimiter(any());
    verify(rateLimiter, never()).tryAcquire(any(), any());
  }

  @Test
  void whenKeyInDB_thenCheckAndUpdateShouldReturnCheck() {
    when(ruleRepository.findById(entity.key())).thenReturn(Optional.of(entity));
    when(rateLimiterRegistry.getRateLimiter(RateLimiterAlgorithm.TOKEN_BUCKET)).thenReturn(rateLimiter);
    when(rateLimiter.tryAcquire(entity.key(), entity.parameters())).thenReturn(check);

    CheckDTO result = checkService.checkAndUpdate(entity.key());
    assertEquals(check, result);

    verify(rateLimiterRegistry).getRateLimiter(RateLimiterAlgorithm.TOKEN_BUCKET);
    verify(rateLimiter).tryAcquire(entity.key(), entity.parameters());
  }

}
