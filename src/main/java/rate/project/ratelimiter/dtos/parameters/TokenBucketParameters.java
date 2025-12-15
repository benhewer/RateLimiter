package rate.project.ratelimiter.dtos.parameters;

import org.jetbrains.annotations.NotNull;

public record TokenBucketParameters(
        int capacity,
        int refillRate
) implements AlgorithmParameters {

  @Override
  public @NotNull String toString() {
    return "TokenBucketParameters{" +
            "capacity=" + capacity +
            ", refillRate=" + refillRate +
            '}';
  }

}
