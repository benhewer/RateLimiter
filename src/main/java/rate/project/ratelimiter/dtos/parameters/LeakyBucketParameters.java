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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LeakyBucketParameters that = (LeakyBucketParameters) o;
    return capacity == that.capacity && outflowRate == that.outflowRate;
  }

}
