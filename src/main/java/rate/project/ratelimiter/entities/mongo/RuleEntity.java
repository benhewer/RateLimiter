package rate.project.ratelimiter.entities.mongo;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;

/**
 * Represents a rate limiter rule, to be stored in MongoDB.
 * Has a one-to-one relationship with the RuleDTO class.
 * Indexed on projectId and ruleKey (combined they are unique).
 */
@Document(collection = "rules")
@CompoundIndex(name = "project_rule_idx", def = "{'projectId': 1, 'ruleKey': 1}", unique = true)
public record RuleEntity(
        @Id
        String id,
        @Field("project_id")
        String projectId,
        @Field("rule_key")
        String ruleKey,
        RateLimiterAlgorithm algorithm,
        AlgorithmParameters parameters
) {
  @Override
  public @NotNull String toString() {
    return "RuleEntity{" +
            "projectId='" + projectId + '\'' +
            ", ruleKey='" + ruleKey + '\'' +
            ", algorithm=" + algorithm +
            ", parameters=" + parameters +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RuleEntity that = (RuleEntity) o;
    return projectId.equals(that.projectId)
            && ruleKey.equals(that.ruleKey)
            && algorithm == that.algorithm
            && parameters.equals(that.parameters);
  }

}
