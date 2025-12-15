package rate.project.ratelimiter.dtos.parameters;

public sealed interface AlgorithmParameters permits
        LeakyBucketParameters,
        TokenBucketParameters {
}
