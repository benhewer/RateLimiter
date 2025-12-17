package rate.project.ratelimiter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.repositories.redis.RateLimiterStateRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TokenBucketServiceTests {

  @Mock
  private RateLimiterStateRepository stateRepository;

  @InjectMocks
  private TokenBucketService service;

  @Test
  void testFillBucket() {
    String key = "testBucket";
    long capacity = 10;

    service.fillBucket(key, capacity);

    // Ensure stateRepository.save() was called once with the correct arguments
    ArgumentCaptor<RateLimiterState> captor = ArgumentCaptor.forClass(RateLimiterState.class);
    verify(stateRepository, times(1)).save(captor.capture());

    RateLimiterState state = captor.getValue();
    assertEquals(key, state.getKey());
    assertEquals(capacity, state.getLevel());
    assertTrue(state.getLastUpdateTime() > 0);
  }

}
