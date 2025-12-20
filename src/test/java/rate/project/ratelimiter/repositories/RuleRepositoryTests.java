package rate.project.ratelimiter.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import rate.project.ratelimiter.config.TestMongoConfig;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestMongoConfig.class)
public class RuleRepositoryTests {

  @Autowired
  private RuleRepository repository;

  @Test
  void repositoryShouldStartEmpty() {
    assertEquals(0, repository.count());
  }

  @Test
  void saveShouldPersistRule() {
    RuleEntity rule = new RuleEntity(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );

    repository.save(rule);

    RuleEntity found = repository.findById(rule.key()).orElse(null);
    assertNotNull(found);
    assertEquals(rule, found);

    assertEquals(1, repository.count());
  }

}
