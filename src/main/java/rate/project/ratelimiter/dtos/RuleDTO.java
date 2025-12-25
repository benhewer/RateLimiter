package rate.project.ratelimiter.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;

/**
 * This class represents a rate limiter rule. It is convertible to and from JSON.
 * The JSON format is as follows:
 * {
 *   "rule_key": "login"
 *   "algorithm": "TOKEN_BUCKET",
 *   "parameters": {
 *     "capacity": 10,
 *     "refillRate": 1
 *   }
 * }
 */
public record RuleDTO(
        String ruleKey,
        RateLimiterAlgorithm algorithm,
        // Since AlgorithmParameters is polymorphic, we need to use Jackson's
        // type information to deserialize it, choosing the correct subtype.
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "algorithm"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(
                        value = TokenBucketParameters.class,
                        name = "TOKEN_BUCKET"
                ),
                @JsonSubTypes.Type(
                        value = LeakyBucketParameters.class,
                        name = "LEAKY_BUCKET"
                )
        })
        AlgorithmParameters parameters
) {

  // Ensure that the rule has been created with parameters that match the algorithm
  public RuleDTO {
    if (!algorithmMatchesParameters(algorithm, parameters)) {
      throw new IllegalArgumentException(
              "Algorithm " + algorithm + " does not match parameters " + parameters.getClass().getSimpleName()
      );
    }
  }

  private boolean algorithmMatchesParameters(RateLimiterAlgorithm algorithm, AlgorithmParameters parameters) {
    return switch (algorithm) {
      case TOKEN_BUCKET -> parameters instanceof TokenBucketParameters;
      case LEAKY_BUCKET -> parameters instanceof LeakyBucketParameters;
    };
  }

  @Override
  public @NotNull String toString() {
    return "RuleDTO{" +
            "ruleKey='" + ruleKey + '\'' +
            ", algorithm=" + algorithm +
            ", parameters=" + parameters +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RuleDTO ruleDTO = (RuleDTO) o;
    return ruleKey.equals(ruleDTO.ruleKey)
            && algorithm == ruleDTO.algorithm
            && parameters.equals(ruleDTO.parameters);
  }

}
