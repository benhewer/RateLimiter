package rate.project.ratelimiter.factories;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisScriptFactory {

    public RedisScript<@NotNull List<Long>> tokenBucketScript() {
        return getRedisScript("scripts/token_bucket.lua");
    }

    public RedisScript<@NotNull List<Long>> leakyBucketScript() {
        return getRedisScript("scripts/leaky_bucket.lua");
    }

    @SuppressWarnings("unchecked")
    private RedisScript<@NotNull List<Long>> getRedisScript(String filepath) {
        return RedisScript.of(
                new ClassPathResource(filepath),
                (Class<List<Long>>) (Class<?>) List.class
        );
    }

}
