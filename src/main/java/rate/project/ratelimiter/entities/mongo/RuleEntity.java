package rate.project.ratelimiter.entities.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rate.project.ratelimiter.dtos.parameters.AlgorithmParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;

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

}
