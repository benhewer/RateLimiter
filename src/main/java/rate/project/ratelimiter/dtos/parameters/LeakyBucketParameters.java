package rate.project.ratelimiter.dtos.parameters;

import org.jetbrains.annotations.NotNull;

public record LeakyBucketParameters(
        int capacity,
        int outflowRate
) implements AlgorithmParameters {

  @Override
  public @NotNull String toString() {
    return "TokenBucketParameters{" +
            "capacity=" + capacity +
            ", outflowRate=" + outflowRate +
            '}';
  }

}
