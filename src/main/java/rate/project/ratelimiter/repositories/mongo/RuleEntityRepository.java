package rate.project.ratelimiter.repositories.mongo;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import rate.project.ratelimiter.entities.mongo.RuleEntity;

public interface RuleEntityRepository extends MongoRepository<@NotNull RuleEntity, @NotNull String> { }
