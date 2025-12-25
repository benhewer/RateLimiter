package rate.project.ratelimiter.dtos;

/**
 * Used to accept JSON of the form:
 * {
 *   "user_key": potassiumlover33
 * }
 * @param userKey The key for the user being limited.
 */
public record CheckRequest(String userKey) { }
