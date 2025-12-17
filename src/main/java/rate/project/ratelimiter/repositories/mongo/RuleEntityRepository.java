package rate.project.ratelimiter.repositories.mongo;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rate.project.ratelimiter.entities.mongo.RuleEntity;

/**
 * A way to interact with the RuleEntity collection in MongoDB.
 */
@Repository
public interface RuleEntityRepository extends MongoRepository<@NotNull RuleEntity, @NotNull String> { }
