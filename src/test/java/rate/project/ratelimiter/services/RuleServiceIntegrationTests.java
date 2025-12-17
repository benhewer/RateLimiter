package rate.project.ratelimiter.services;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import rate.project.ratelimiter.config.TestMongoConfig;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.entities.mongo.RuleEntity;
import rate.project.ratelimiter.entities.redis.RateLimiterState;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({TestRedisConfig.class, TestMongoConfig.class})
public class RuleServiceIntegrationTests {

  @Autowired
  private RuleService service;

  @Autowired
  private RuleEntityService ruleEntityService;

  @Autowired
  private RateLimiterStateService rateLimiterStateService;

  @Autowired
  private RedisTemplate<String, RateLimiterState> testRedisTemplate;

  @Autowired
  private MongoClient testMongoClient;

  @Test
  void mongoAndRedisShouldRunInContainers() {
    LettuceConnectionFactory factory = (LettuceConnectionFactory) testRedisTemplate.getConnectionFactory();
    assertNotNull(factory);

    // Ensure Redis is not running locally (on default port)
    assertNotEquals(6379, factory.getPort());

    // Ensure Mongo is not running locally (on default port)
    testMongoClient.getClusterDescription().getClusterSettings().getHosts().forEach(serverAddress -> {
      assertNotEquals(27017, serverAddress.getPort());
    });
  }

  @Test
  void createRuleShouldPersistRuleToMongoAndRedis() {
    RuleDTO rule = new RuleDTO(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.LEAKY_BUCKET,
            new LeakyBucketParameters(10, 1)
    );

    service.createRule(rule);

    // Ensure the rule was persisted to Mongo
    RuleEntity ruleEntity = ruleEntityService.getRule(rule.key());
    assertNotNull(ruleEntity);
    assertEquals(RateLimiterAlgorithm.LEAKY_BUCKET, ruleEntity.algorithm());

    // Ensure the rule was persisted to Redis
    RateLimiterState state = rateLimiterStateService.getState(rule.key());
    assertNotNull(state);
    assertEquals(0, state.getLevel());
  }

}
