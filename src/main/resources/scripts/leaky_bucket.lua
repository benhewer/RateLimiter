-- Script to atomically check the level of water in the bucket,
-- leak water if necessary,
-- then add water and return true, or, if full, return false

local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local outflowRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- If the key is not present, water = 0, otherwise set tokens to the redis value
local water = tonumber(redis.call('HGET', key, 'water')) or 0

-- Compute elapsed time and leak water
local lastRefillTime = tonumber(redis.call('HGET', key, 'lastRefill')) or 0
local elapsed = now - lastRefillTime

local waterLost = elapsed * outflowRate / 1000
if waterLost >= 1 then
    water = math.max(water - waterLost, 0)
    redis.call('HSET', key, 'lastRefill', now)
end

-- Add one to water level if possible
if water <= capacity - 1 then
    water = water + 1
    redis.call('HSET', key, 'water', water)
    local remaining = capacity - water
    return {
        1,              -- allowed (true)
        remaining,      -- remaining
        0               -- retry after ms
    }
else
    local retryAfterMs = 1000 / outflowRate - elapsed
    local remaining = capacity - water
    return {
        0,              -- allowed (false)
        remaining,      -- remaining
        retryAfterMs    -- retry after ms
    }
end