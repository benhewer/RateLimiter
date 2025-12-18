package rate.project.ratelimiter.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.repositories.redis.RateLimiterStateRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestRedisConfig.class)
public class RateLimiterStateRepositoryTests {

  @Autowired
  private RateLimiterStateRepository repository;

  @Test
  void repositoryShouldStartEmpty() {
    assertEquals(0, repository.count());
  }

  @Test
  void repositoryShouldPersistState() {
    RateLimiterState state
            = new RateLimiterState("key:potassiumlover33:login", 10, 0);

    repository.save(state);

    RateLimiterState found = repository.findById(state.getKey()).orElse(null);
    assertNotNull(found);
    assertEquals(state, found);
    assertEquals(1, repository.count());
  }

}
