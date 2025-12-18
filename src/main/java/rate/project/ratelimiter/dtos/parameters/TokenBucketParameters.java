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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TokenBucketParameters that = (TokenBucketParameters) o;
    return capacity == that.capacity && refillRate == that.refillRate;
  }

}
