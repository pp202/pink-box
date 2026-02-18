# corck-board monorepo (single artifact stack)

A full-stack monorepo with:
- `frontend/`: Vue 3 + Vite + TypeScript SPA
- `backend/`: Spring Boot 3 (Java 17, Maven) API + static asset hosting
- `docker-compose.yml`: local Postgres

In production, Spring Boot serves both API and built SPA from one deployable JAR.

## Repository structure

```text
my-repo/
  frontend/
  backend/
  docker-compose.yml
  README.md
  .gitignore
```

## Prerequisites

- Java 17
- Maven 3.9+
- Docker + Docker Compose
- Node is only required for standalone frontend dev (`npm install && npm run dev`)
  - For production build, Maven installs/pins Node and npm via `frontend-maven-plugin`.

## Start Postgres

```bash
docker compose up -d
```

Default DB settings:
- database: `corckboard`
- username: `corckboard`
- password: `corckboard`
- host/port: `localhost:5432`

## Development mode

Run backend and frontend separately.

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Vite proxies `/api` to `http://localhost:8080`, so no CORS config is needed in dev.

## Production / single-artifact run

Build and run only the backend JAR. During package:
- Maven installs Node/npm (pinned)
- runs `npm ci` + `npm run build` in `frontend/`
- copies `frontend/dist` into backend static resources

```bash
cd backend
mvn clean package
java -jar target/*.jar
```

Open: `http://localhost:8080`

### Skip frontend build when needed

```bash
mvn clean package -DskipFrontend=true
```

## Authentication behavior

- Cookie-based session auth (Spring Security session + `JSESSIONID` HttpOnly cookie)
- No JWT/localStorage tokens
- Demo user is seeded by Flyway migration:
  - username: `demo`
  - password: `demo123`

### CSRF

CSRF is enabled for cookie-session safety with `CookieCsrfTokenRepository`.
Frontend first calls `GET /api/v1/csrf`, then sends `X-CSRF-TOKEN` header on state-changing requests.

## API endpoints

- Public:
  - `GET /api/v1/health`
  - `GET /api/v1/csrf`
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/logout`
- Auth required:
  - `GET /api/v1/auth/me`
  - `GET /api/v1/hello`
  - `GET /api/v1/notes`
  - `POST /api/v1/notes`

## Example curl flow

```bash
# 1) Fetch CSRF token + cookie
curl -i -c cookies.txt http://localhost:8080/api/v1/csrf

# 2) Login (replace TOKEN from previous response JSON)
curl -i -b cookies.txt -c cookies.txt \
  -H "Content-Type: application/json" \
  -H "X-CSRF-TOKEN: TOKEN" \
  -d '{"username":"demo","password":"demo123"}' \
  http://localhost:8080/api/v1/auth/login

# 3) Current user
curl -i -b cookies.txt http://localhost:8080/api/v1/auth/me

# 4) Create note (include latest CSRF token)
curl -i -b cookies.txt \
  -H "Content-Type: application/json" \
  -H "X-CSRF-TOKEN: TOKEN" \
  -d '{"title":"First note","body":"Created from curl"}' \
  http://localhost:8080/api/v1/notes
```

## Troubleshooting

- **Cannot connect to DB**:
  - Ensure `docker compose up -d` is running.
  - Verify backend env vars if using non-default DB values:
    - `DB_URL`
    - `DB_USERNAME`
    - `DB_PASSWORD`
- **Flyway migration issues**:
  - Check `backend/src/main/resources/db/migration` scripts.
  - Reset local DB volume if schema is out of sync:
    ```bash
    docker compose down -v
    docker compose up -d
    ```
- **Frontend not embedded in jar**:
  - Ensure `mvn clean package` ran without `-DskipFrontend=true`.
