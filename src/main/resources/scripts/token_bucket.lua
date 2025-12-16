-- Script to atomically check the number of tokens for the given key,
-- refill the tokens if necessary,
-- then consume a token and return true, or, if none left, return false

local tokens = tonumber(redis.call('GET', KEYS[1]))
local now = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local refillRate = tonumber(ARGV[3])

-- Compute elapsed time and refill tokens
local lastRefillTime = tonumber(redis.call('HGET', KEYS[1], 'lastRefill') or 0)
local elapsed = now - lastRefillTime
tokens = math.min(tokens + (elapsed * refillRate / 1000), capacity)

-- Consume one token if available
if tokens >= 1 then
	tokens = tokens - 1
	redis.call('SET', KEYS[1], tokens)
	redis.call('HSET', KEYS[1], 'lastRefill', now)
	return 1
else
	return 0
end
