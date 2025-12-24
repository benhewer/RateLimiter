package rate.project.ratelimiter.dtos;

/**
 * Represents the response from a rate limiter.
 * This information will be returned to the client on POST /check.
 * It is convertible to and from JSON.
 * @param allowed Whether the request is allowed or not.
 * @param remaining The number of requests remaining in the current window.
 * @param retryAfterMillis The number of milliseconds until the next window begins.
 */
public record CheckDTO(boolean allowed, long remaining, long retryAfterMillis) {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CheckDTO that = (CheckDTO) o;
    return that.allowed == allowed && that.remaining == remaining && that.retryAfterMillis == retryAfterMillis;
  }

}
