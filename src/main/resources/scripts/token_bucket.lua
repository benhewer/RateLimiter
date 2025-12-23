-- Script to atomically check the number of tokens for the given key,
-- refill the tokens if necessary,
-- then consume a token and return true, or, if none left, return false

local key = tonumber(KEYS[1]);

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- If the key is not present, tokens = capacity, otherwise set tokens to the redis value
local tokens = tonumber(redis.call('HGET', key, 'tokens')) or capacity

-- Compute elapsed time and refill tokens
local lastRefillTime = tonumber(redis.call('HGET', key, 'lastRefill') or 0)
local elapsed = now - lastRefillTime
tokens = math.min(tokens + (elapsed * refillRate / 1000), capacity)

-- Consume one token if available
if tokens >= 1 then
	tokens = tokens - 1
	redis.call('HSET', key, 'tokens', tokens)
	redis.call('HSET', key, 'lastRefill', now)
	return {
        1,           -- allowed (true)
        tokens,      -- remaining
        0            -- retry after ms
    }
else
    local retryAfterMs = 1000 / refillRate - elapsed
	return {
        0,           -- allowed (false)
        tokens,      -- remaining
        retryAfterMs -- retry after ms
    }
end
