# Distributed Rate Limiter

A high-performance, distributed rate-limiting service built with Spring Boot 4, Redis, and MongoDB. This project ensures shared state across multiple nodes, making it ideal for microservice architectures that require consistent request throttling.

The application is fully tested, using Testcontainers for MongoDB and Redis so tests can easily be ran from any machine.

## API Reference

### Rule Management (CRUD)
Manage the definitions of your rate limits.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/projects/{projectId}/rules` | Create a new rate limit rule. |
| `GET` | `/projects/{projectId}/rules/{ruleKey}` | Get a rate limit rule |
| `PUT` | `/projects/{projectId}/rules/{ruleKey}` | Update a rate limit rule. |
| `DELETE` | `/projects/{projectId}/rules/{ruleKey}` | Remove a rate limit rule. |

Note that the combination of projectId and ruleKey must be unique, as they identify a rule.

The rule is of the form:
```json
{
  "ruleKey": "login",
  "algorithm": "TOKEN_BUCKET",
  "parameters": {
    "capacity": 10,
    "refillRate": 1
  }
}
```

As you can see, the rules allow for different algorithms with different parameters to be used as the rate limiter.
Currently the **Token Bucket** and **Leaky Bucket** algorithms have been implemented.

### Rate Limit Enforcement
Used to check if a request is allowed, and return other useful data such as remaining requests or time until the next request.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/projects/{projectId}/rules/{ruleKey}/check` | Checks data in Redis, and returns an appropriate response. |

---

## How It Works (Distributed Architecture)

1.  **Rule Retrieval**: On a `/check` request, the service identifies the rule configuration from MongoDB (cached with Caffeine for performance).
2.  **Atomic Redis**: The service communicates with Redis using a atomic lua scripts to handle the logic of the rate limiter algorithm.
3.  **Shared State**: Because all instances of this service point to the same Redis cluster, the state is shared. If Node A processes a request, Node B immediately knows the remaining usage for that user.

---

## How To Use

The project is fully dockerized, so simply spin up all containers necessary with: 

```docker-compose up``` 

This will build and run the application. It will be available at `http://localhost:8080`.
