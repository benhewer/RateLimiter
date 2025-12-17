package rate.project.ratelimiter.dtos.parameters;

/**
 * Represents the parameters of a rate limiter algorithm.
 */
public sealed interface AlgorithmParameters permits
        LeakyBucketParameters,
        TokenBucketParameters {
}
