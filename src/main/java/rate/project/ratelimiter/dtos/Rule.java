package rate.project.ratelimiter.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.dtos.parameters.LeakyBucketParameters;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;

/**
 * This class represents a rate limiter rule. It is convertible to and from JSON.
 * The JSON format is as follows:
 * {
 *   "key": "user:potassiumlover33:login",
 *   "algorithm": "TOKEN_BUCKET",
 *   "parameters": {
 *     "capacity": 10,
 *     "refillRate": 1
 *   }
 * }
 */
public record Rule(
        String key,
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
  public Rule {
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
    return "Rule{" +
            "key='" + key + '\'' +
            ", algorithm=" + algorithm +
            ", parameters=" + parameters +
            '}';
  }

  public enum RateLimiterAlgorithm {
    TOKEN_BUCKET,
    LEAKY_BUCKET
  }

}

