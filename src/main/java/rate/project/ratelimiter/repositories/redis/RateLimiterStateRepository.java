package rate.project.ratelimiter.repositories.redis;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rate.project.ratelimiter.entities.redis.RateLimiterState;

/**
 * A way to interact with the RateLimiterState collection in Redis.
 */
@Repository
public interface RateLimiterStateRepository extends CrudRepository<@NotNull RateLimiterState, @NotNull String> { }
