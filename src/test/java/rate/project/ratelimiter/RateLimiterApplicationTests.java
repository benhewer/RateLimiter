package rate.project.ratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import rate.project.ratelimiter.config.TestMongoConfig;
import rate.project.ratelimiter.config.TestRedisConfig;

@SpringBootTest
@Import({TestMongoConfig.class, TestRedisConfig.class})
class RateLimiterApplicationTests {

  @Test
  void contextLoads() {
  }

}
