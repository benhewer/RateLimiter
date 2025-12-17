package rate.project.ratelimiter.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import rate.project.ratelimiter.config.TestMongoConfig;
import rate.project.ratelimiter.config.TestRedisConfig;
import rate.project.ratelimiter.dtos.ApiResponse;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({TestRedisConfig.class, TestMongoConfig.class})
public class RuleControllerIntegrationTests {

  @Autowired
  private RuleController controller;

  @Test
  void postRuleShouldReturnErrorIfRuleAlreadyExists() {
    RuleDTO rule = new RuleDTO(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(
                    10,
                    1
            )
    );

    // The first POST should succeed
    ApiResponse<RuleDTO> response = controller.addRule(rule).getBody();
    assertNotNull(response);
    assertNull(response.getError());
    assertEquals(response.getData().key(), rule.key());

    // The second POST should fail
    response = controller.addRule(rule).getBody();
    assertNotNull(response);
    assertNull(response.getData());
    assertNotNull(response.getError());
  }

}
