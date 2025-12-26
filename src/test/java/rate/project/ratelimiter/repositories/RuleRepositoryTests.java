package rate.project.ratelimiter.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import rate.project.ratelimiter.config.TestMongoConfig;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.repositories.mongo.RuleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import({TestMongoConfig.class, TestRedisConfig.class})
public class RuleRepositoryTests {

  @Autowired
  private RuleRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @Test
  void repositoryShouldStartEmpty() {
    assertEquals(0, repository.count());
  }

  @Test
  void saveShouldPersistRule() {
    RuleEntity rule = new RuleEntity(
            null,
            "example",
            "login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );

    repository.save(rule);

    RuleEntity found = repository.findByProjectIdAndRuleKey(rule.projectId(), rule.ruleKey()).orElse(null);
    assertNotNull(found);
    assertEquals(rule, found);

    assertEquals(1, repository.count());
  }

}
