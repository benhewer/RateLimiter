package rate.project.ratelimiter.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

public class TestMongoConfig {

  private static final MongoDBContainer mongoContainer =
          new MongoDBContainer("mongo:latest")
                  .withExposedPorts(27017);

  static {
    mongoContainer.start();
  }

  @Bean
  public MongoClient mongoClient() {
    return MongoClients.create(mongoContainer.getReplicaSetUrl());
  }

}
