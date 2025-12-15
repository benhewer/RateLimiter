package rate.project.ratelimiter.dtos;

import org.junit.jupiter.api.Test;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class RuleTests {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void jsonShouldDeserializeToRule() {
    String json = """
            {
              "key": "user:potassiumlover33:login",
              "algorithm": "TOKEN_BUCKET",
              "parameters": {
                "capacity": 10,
                "refillRate": 1
              }
            }
            """;

    Rule rule = objectMapper.readValue(json, Rule.class);

    assertEquals("user:potassiumlover33:login", rule.key());
    assertEquals(Rule.RateLimiterAlgorithm.TOKEN_BUCKET, rule.algorithm());

    assertInstanceOf(TokenBucketParameters.class, rule.parameters());

    TokenBucketParameters params =
            (TokenBucketParameters) rule.parameters();

    assertEquals(10, params.capacity());
    assertEquals(1, params.refillRate());
  }

  @Test
  void ruleShouldSerializeToJson() {
    Rule rule = new Rule(
            "user:potassiumlover33:login",
            Rule.RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );

    String json = objectMapper.writeValueAsString(rule);

    assertEquals("""
                    {"key":"user:potassiumlover33:login","algorithm":"TOKEN_BUCKET","parameters":{"capacity":10,"refillRate":1}}""",
            json);
  }

  @Test
  void ruleShouldThrowWhenAlgorithmDoesNotMatchParameter() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
      Rule invalidRule = new Rule(
              "user:potassiumlover33:login",
              Rule.RateLimiterAlgorithm.LEAKY_BUCKET,
              new TokenBucketParameters(10, 1)
      );
    });

    assertEquals(
            "Algorithm LEAKY_BUCKET does not match parameters TokenBucketParameters",
            e.getMessage()
    );
  }

}
