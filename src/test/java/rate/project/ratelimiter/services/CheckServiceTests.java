package rate.project.ratelimiter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.factories.RateLimiterFactory;
import rate.project.ratelimiter.services.ratelimiters.RateLimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckServiceTests {

  @Mock
  private RateLimiterFactory rateLimiterFactory;

  @Mock
  private RateLimiter rateLimiter;

  @InjectMocks
  private CheckService checkService;

  private final String projectId = "example";
  private final String ruleKey = "login";
  private final String userKey = "potassiumlover33";

  private final CheckResponse check = new CheckResponse(true, 9, 0);

  @Test
  void whenKeyNotInDB_thenCheckAndUpdateShouldReturnNull() {
    // Mock when the key is not in DB
    when(rateLimiterFactory.getRateLimiter(projectId, ruleKey)).thenReturn(null);
    CheckResponse rule = checkService.checkAndUpdate(projectId, ruleKey, userKey);
    assertNull(rule);

    verify(rateLimiterFactory).getRateLimiter(anyString(), anyString());
    verify(rateLimiter, never()).tryAcquire(anyString());
  }

  @Test
  void whenKeyInDB_thenCheckAndUpdateShouldReturnCheck() {
    when(rateLimiterFactory.getRateLimiter(projectId, ruleKey)).thenReturn(rateLimiter);
    when(rateLimiter.tryAcquire(userKey)).thenReturn(check);

    CheckResponse result = checkService.checkAndUpdate(projectId, ruleKey, userKey);
    assertEquals(check, result);

    verify(rateLimiterFactory).getRateLimiter(projectId, ruleKey);
    verify(rateLimiter).tryAcquire(userKey);
  }

}
