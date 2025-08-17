# 🚀 RediServe

RediServe is a **distributed cache + DB fetcher microservices playground**, built with:
- **Java 17 + Spring Boot 3**
- **Spring Cloud Config** (centralized configuration)
- **Postgres** (for persistence)
- **Redis** (for caching)
- **Docker Compose** (for local infra)

This is a pet project to learn and experiment with:
- Horizontal scalability
- Cache-aside and read-through patterns
- Service orchestration
- Configurable caching behavior

---

## 🟢 Phase 0 – Foundations
Goal: get a **bare-bones environment running**.

### 📂 Project Structure
```

RediServe/
├── pom.xml                   # Parent Maven POM (manages versions + dependency management)
├── config-repo/              # Centralized configs (picked up by Spring Cloud Config Server)
│   ├── cache-gateway.yml
│   └── db-fetcher.yml
├── config-server/            # Spring Cloud Config Server
├── services/
│   ├── cache-gateway/        # Entrypoint for clients (talks to Redis + DB fetcher)
│   └── db-fetcher/           # Fetches from Postgres, source of truth for data
└── docker-compose.yml        # Infra: Redis + Postgres

````

---

### 🔧 Prerequisites
- Java 17+
- Maven 3.9+
- Docker + Docker Compose
- (Optional) Homebrew for `redis-cli` / `psql`

---

### ▶️ Running Infra

Bring up Postgres & Redis:

```bash
docker compose up -d
````

Check running containers:

```bash
docker ps
```

You should see:

* `rediserve-postgres`
* `rediserve-redis`

---

### 🧪 Testing Redis

```bash
docker exec -it rediserve-redis redis-cli ping
# PONG
```

---

### 🧪 Testing Postgres

```bash
docker exec -it rediserve-postgres psql -U postgres -d rediserve
\conninfo;
```

---

### ▶️ Running Services

Start **Config Server** first:

```bash
mvn spring-boot:run -pl config-server
```

Then run the apps:

```bash
mvn spring-boot:run -pl services/cache-gateway
mvn spring-boot:run -pl services/db-fetcher
```

Check configs being served:

```
http://localhost:8888/cache-gateway/default
http://localhost:8888/db-fetcher/default
```

---

## ✅ Deliverables

### Phase 0

* [x] Infra running (`docker compose up` works, Redis + Postgres healthy)
* [x] Empty Spring Boot services build and start
* [x] Verified connectivity (`PONG` from Redis, `\conninfo` from Postgres)

### Phase 1

* [x] Config Server running (`config-server/`)
* [x] Centralized config files in `config-repo/`
* [x] `db-fetcher` exposes REST endpoint to fetch products from Postgres
* [x] `cache-gateway` fetches from Redis, falls back to `db-fetcher` on cache-miss
* [x] Redis caching with TTL (60s, configurable)
* [x] Health endpoints exposed (`/actuator/health`)

---

## 🏗️ Next Steps (Phase 2)

* Add **orchestrator service** for managing cache policies
* Make TTLs configurable per entity via config-repo
* Add Docker Compose definitions for all services (cache-gateway, db-fetcher, config-server)
* Add integration tests (Redis + Postgres + services)
* Explore **service discovery** (Eureka / Consul) instead of hardcoding hostnames

```