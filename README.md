# 🚀 RediServe

RediServe is a **distributed cache + DB fetcher microservices playground**, built with:
- **Java 17 + Spring Boot 3**
- **Spring Cloud Config** (centralized configuration)
- **Postgres** (for persistence)
- **Redis** (for caching)
- **Apache Kafka** (for async cache policy + invalidation events)
- **Docker Compose** (for local infra)

This is a pet project to learn and experiment with:
- Horizontal scalability
- Cache-aside and read-through patterns
- Centralized cache policy management
- Event-driven orchestration with Kafka
- Configurable caching behavior
- Hot-reloading configs without redeploy

---

## 📂 Project Structure

```
RediServe/
├── pom.xml                   # Parent Maven POM (manages versions + dependency management)
├── config-repo/              # Centralized configs (picked up by Spring Cloud Config Server)
│   ├── cache-gateway.yml
│   ├── db-fetcher.yml
│   └── orchestrator.yml
├── config-server/            # Spring Cloud Config Server
├── services/
│   ├── cache-gateway/        # Entrypoint for clients (talks to Redis + DB fetcher, listens to Kafka)
│   ├── db-fetcher/           # Fetches from Postgres, source of truth for data
│   └── orchestrator/         # Publishes cache policy + invalidation events via Kafka
└── docker-compose.yml        # Infra: Redis + Postgres + Kafka + Zookeeper
```

---

## 🔧 Prerequisites

* Java 17+
* Maven 3.9+
* Docker + Docker Compose
* (Optional) Homebrew for `redis-cli` / `psql`
* `curl` or Postman for testing

---

## ▶️ Running Infra

Bring up Postgres, Redis, Kafka, and Zookeeper:

```bash
docker compose up -d
```

Check running containers:

```bash
docker ps
```

You should see:

* `rediserve-postgres`
* `rediserve-redis`
* `rediserve-kafka`
* `rediserve-zookeeper`

---

## 🧪 Testing Infra

### Redis

```bash
docker exec -it rediserve-redis redis-cli ping
# PONG
```

### Postgres

```bash
docker exec -it rediserve-postgres psql -U postgres -d rediserve
\conninfo;
```

### Kafka

```bash
docker exec -it rediserve-kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```

You should see topics like:

* `cache.policy.updates`
* `cache.namespace.invalidate`

---

## ▶️ Running Services

Start **Config Server** first:

```bash
mvn spring-boot:run -pl config-server
```

Then run the apps:

```bash
mvn spring-boot:run -pl services/db-fetcher
mvn spring-boot:run -pl services/cache-gateway
mvn spring-boot:run -pl services/orchestrator
```

Check configs being served:

```
http://localhost:8888/cache-gateway/default
http://localhost:8888/db-fetcher/default
http://localhost:8888/orchestrator/default
```

---

## 📡 REST Endpoints

### 1. Fetch Product (Cache-Gateway)

```bash
curl -X GET "http://localhost:8081/cache/shop/products/1"
```

* Reads from Redis if present.
* Falls back to `db-fetcher` on cache miss (read-through).

---

### 2. Update Policy (via Orchestrator → Kafka → Cache-Gateway)

```bash
curl -X POST "http://localhost:8083/admin/namespaces/shop/policy" \
  -H "Content-Type: application/json" \
  -d '{"ttlSeconds": 5, "consistencyMode": "READ_THROUGH"}'
```

* Orchestrator publishes to `cache.policy.updates`.
* Cache-Gateway listens and updates its in-memory policy registry dynamically.
* **No restart needed**.

---

### 3. Invalidate Namespace (via Orchestrator → Kafka → Cache-Gateway)

```bash
curl -X POST "http://localhost:8083/admin/namespaces/shop/invalidate"
```

* Orchestrator publishes to `cache.namespace.invalidate`.
* Cache-Gateway listens and deletes all matching Redis keys.

---

## ✅ Deliverables

### Phase 0 – Foundations

* [x] Infra running (`docker compose up` works, Redis + Postgres healthy)
* [x] Empty Spring Boot services build and start
* [x] Verified connectivity (`PONG` from Redis, `\conninfo` from Postgres)

### Phase 1 – Config Server + Cache Basics

* [x] Config Server running (`config-server/`)
* [x] Centralized config files in `config-repo/`
* [x] `db-fetcher` exposes REST endpoint to fetch products from Postgres
* [x] `cache-gateway` fetches from Redis, falls back to `db-fetcher` on cache-miss
* [x] Redis caching with **type-safe serialization** using `GenericJackson2JsonRedisSerializer`
* [x] TTL is **configurable per namespace/entity** via policy registry
* [x] Health endpoints exposed (`/actuator/health`)

### Phase 2 – Orchestrator + Kafka

* [x] Added **orchestrator service** for centralized cache admin
* [x] Kafka topics for cache policy + invalidation events
* [x] Gateway listens on Kafka and applies changes dynamically
* [x] REST → Kafka → Gateway flow verified with curl

### Phase 3 – Config on the Fly

* [x] Hot reload configs with **Spring Cloud Config** + `@RefreshScope`
* [x] Policies updated at runtime via Orchestrator
* [x] Cache-Gateway applies new TTLs / consistency modes **without restart**
* [x] Deliverable: Change caching rules dynamically

---

## 🏗️ Next Steps (Phase 4)

* Add Docker Compose definitions for all services (cache-gateway, db-fetcher, orchestrator, config-server)
* Add integration tests (Redis + Postgres + Kafka + services)
* Explore **service discovery** (Eureka / Consul) instead of hardcoding hostnames
* Add metrics + dashboards (Prometheus + Grafana)
* Explore **write-through** and **write-behind** caching modes
* Add **sequence diagrams** to README for better architecture visibility