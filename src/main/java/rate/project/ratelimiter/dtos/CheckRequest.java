package rate.project.ratelimiter.dtos;

/**
 * Used to accept JSON of the form:
 * {
 *   "key": key
 * }
 * @param key The key being checked
 */
public record CheckRequest(String key) { }
