package rate.project.ratelimiter.entities.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;

/**
 * Represents a rate limiter rule, to be stored in MongoDB.
 * Has a one-to-one relationship with the RuleDTO class.
 */
@Document("rules")
public class RuleEntity {

  @Id
  private final String key;
  private final RateLimiterAlgorithm algorithm;
  private final AlgorithmParameters parameters;

  public RuleEntity(
          String key,
          RateLimiterAlgorithm algorithm,
          AlgorithmParameters parameters
  ) {
    this.key = key;
    this.algorithm = algorithm;
    this.parameters = parameters;
  }

  public String key() {
    return key;
  }

  public RateLimiterAlgorithm algorithm() {
    return algorithm;
  }

  public AlgorithmParameters parameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return "RuleEntity{" +
            "key='" + key + '\'' +
            ", algorithm=" + algorithm +
            ", parameters=" + parameters +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RuleEntity that = (RuleEntity) o;
    return key.equals(that.key) && algorithm == that.algorithm && parameters.equals(that.parameters);
  }

}
