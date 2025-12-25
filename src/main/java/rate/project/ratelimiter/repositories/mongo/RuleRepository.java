package rate.project.ratelimiter.repositories.mongo;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rate.project.ratelimiter.entities.mongo.RuleEntity;

import java.util.Optional;

/**
 * A way to interact with the RuleEntity collection in MongoDB.
 */
@Repository
public interface RuleRepository extends MongoRepository<@NotNull RuleEntity, @NotNull String> {

  boolean existsByProjectIdAndRuleKey(String projectId, String ruleKey);

  Optional<RuleEntity> findByProjectIdAndRuleKey(String projectId, String ruleKey);

  void deleteByProjectIdAndRuleKey(String projectId, String ruleKey);

}
