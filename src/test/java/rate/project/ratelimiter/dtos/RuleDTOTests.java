package rate.project.ratelimiter.dtos;

import org.junit.jupiter.api.Test;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class RuleDTOTests {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void jsonShouldDeserializeToRule() {
    String json = """
            {
              "ruleKey": "login",
              "algorithm": "TOKEN_BUCKET",
              "parameters": {
                "capacity": 10,
                "refillRate": 1
              }
            }
            """;

    RuleDTO rule = objectMapper.readValue(json, RuleDTO.class);

    assertEquals("login", rule.ruleKey());
    assertEquals(RateLimiterAlgorithm.TOKEN_BUCKET, rule.algorithm());

    assertInstanceOf(TokenBucketParameters.class, rule.parameters());

    TokenBucketParameters params =
            (TokenBucketParameters) rule.parameters();

    assertEquals(10, params.capacity());
    assertEquals(1, params.refillRate());
  }

  @Test
  void ruleShouldSerializeToJson() {
    RuleDTO rule = new RuleDTO(
            "login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );

    String json = objectMapper.writeValueAsString(rule);

    assertEquals("""
                    {"ruleKey":"login","algorithm":"TOKEN_BUCKET","parameters":{"capacity":10,"refillRate":1}}""",
            json);
  }

  @Test
  void ruleShouldThrowWhenAlgorithmDoesNotMatchParameter() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
            new RuleDTO(
                    "login",
                    RateLimiterAlgorithm.LEAKY_BUCKET,
                    new TokenBucketParameters(10, 1)
            )
    );

    assertEquals(
            "Algorithm LEAKY_BUCKET does not match parameters TokenBucketParameters",
            e.getMessage()
    );
  }

}
