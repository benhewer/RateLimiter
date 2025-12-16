package rate.project.ratelimiter.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import rate.project.ratelimiter.entities.mongo.RuleEntity;

public interface RuleRepository extends MongoRepository<@NotNull RuleEntity, @NotNull String> { }
