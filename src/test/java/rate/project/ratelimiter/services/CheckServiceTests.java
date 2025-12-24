package rate.project.ratelimiter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.factories.RateLimiterFactory;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckServiceTests {

  @Mock
  private RateLimiterFactory rateLimiterFactory;

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

  private final CheckResponse check = new CheckResponse(true, 9, 0);

  @Test
  void whenKeyNotInDB_thenCheckAndUpdateShouldReturnNull() {
    when(ruleRepository.findById(entity.key())).thenReturn(Optional.empty());

    CheckResponse rule = checkService.checkAndUpdate(entity.key());
    assertNull(rule);

    verify(rateLimiterFactory, never()).getRateLimiter(anyString());
    verify(rateLimiter, never()).tryAcquire(anyString());
  }

  @Test
  void whenKeyInDB_thenCheckAndUpdateShouldReturnCheck() {
    when(ruleRepository.findById(entity.key())).thenReturn(Optional.of(entity));
    when(rateLimiterFactory.getRateLimiter(entity.key())).thenReturn(rateLimiter);
    when(rateLimiter.tryAcquire(entity.key())).thenReturn(check);

    CheckResponse result = checkService.checkAndUpdate(entity.key());
    assertEquals(check, result);

    verify(rateLimiterFactory).getRateLimiter(entity.key());
    verify(rateLimiter).tryAcquire(entity.key());
  }

}
