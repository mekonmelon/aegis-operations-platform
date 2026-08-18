# Aegis

Aegis is a real-time crisis operations platform prototype. The React frontend can run against either the Java Spring Boot REST API or local mock data.

## Run the frontend locally

You need a recent version of [Node.js](https://nodejs.org/) (Node 20 or newer recommended) and npm.

Create a local environment file from the example if you want to override the defaults:

```bash
cp .env.example .env.local
```

Environment variables:

```text
VITE_API_BASE_URL=http://localhost:8080
VITE_DATA_MODE=api
```

`VITE_DATA_MODE=api` uses the Spring Boot backend. If the initial API load fails, the app falls back to mock data and clearly shows `DEMO FALLBACK — backend unavailable`.

`VITE_DATA_MODE=mock` intentionally uses local demo data and shows `DEMO DATA`.

Run the frontend in API mode:

```bash
npm install
VITE_API_BASE_URL=http://localhost:8080 VITE_DATA_MODE=api npm run dev
```

Run the frontend in mock/demo mode:

```bash
npm install
VITE_DATA_MODE=mock npm run dev
```

Open the URL printed by Vite (normally `http://localhost:5173`). To create and inspect a production build:

```bash
npm run build
npm run preview
```

## Run Elasticsearch locally

Backend development mode uses Elasticsearch 8.15.5 through `docker-compose.yml`. This configuration is for local development only.

Start Elasticsearch:

```bash
docker compose up -d
```

Verify Elasticsearch:

```bash
curl http://localhost:9200
```

Stop Elasticsearch while preserving data:

```bash
docker compose down
```

The named Docker volume `aegis-elasticsearch-data` preserves data across container restarts. To intentionally delete all local Elasticsearch demo data:

```bash
docker compose down -v
```

Elasticsearch indices used by Aegis:

```text
aegis-incidents
aegis-resources
aegis-facilities
aegis-recommendations
aegis-state
```

Inspect incidents directly:

```bash
curl 'http://localhost:9200/aegis-incidents/_search?pretty'
```

## Run the backend locally

You need Java 21. The backend is a Spring Boot application in `backend/` and runs on port `8080`.

Default storage mode is Elasticsearch:

```text
aegis.storage=elasticsearch
spring.elasticsearch.uris=http://localhost:9200
```

Start Elasticsearch first, then run:

```bash
cd backend
./gradlew bootRun
```

The API is available at `http://localhost:8080/api`. For example:

```bash
curl http://localhost:8080/api/dashboard
```

Run backend tests:

```bash
cd backend
./gradlew test
```

The standard test suite uses memory mode so it does not require Docker:

```text
aegis.storage=memory
```

Elasticsearch integration tests use Testcontainers and are skipped automatically when Docker is unavailable.

Run the full backend build:

```bash
cd backend
./gradlew build
```

## Project structure

```text
backend/
├── src/main/java/com/aegis/operations/
│   ├── config/             Minimal development CORS configuration
│   ├── controller/         REST controllers for the API contract
│   ├── exception/          Centralized API error handling
│   ├── model/              Java domain models and enums
│   ├── service/            Dashboard reads and recommendation transitions
│   └── store/              Storage abstraction, memory store, Elasticsearch store
└── src/test/               Spring Boot API tests

src/
├── components/             Reusable dashboard panels and presentation
├── data/mockData.ts        Typed, hardcoded prototype dataset
├── hooks/                  React data-loading hook
├── services/
│   ├── apiDataSource.ts    Fetch-backed REST API implementation and DTO mapping
│   ├── dataSource.ts       UI-facing data source contract
│   ├── dataSourceFactory.ts Environment-based API/mock source selection
│   └── mockDataSource.ts   Local demo implementation
├── types/domain.ts         Incident, resource, facility, and dashboard types
├── App.tsx                 Dashboard composition
├── main.tsx                React entry point
└── styles.css              Responsive command-center visual system
```

## Architecture

Dashboard components receive typed data through props and do not import mock values directly. `OperationsDataSource` is the boundary between the application and its data. `ApiOperationsDataSource` fulfills that contract from the Spring Boot REST API, while `MockOperationsDataSource` fulfills it from local data. `dataSourceFactory.ts` selects the active source from Vite environment variables.

The map is intentionally a self-contained CSS visualization. Its incident and facility markers are populated from the same typed dashboard data and require no map service or network connection.

The backend implements the documented REST contract in `docs/api-contract.md`. `OperationsService` depends on the `OperationsStore` abstraction instead of talking directly to Elasticsearch. Normal development uses `ElasticsearchOperationsStore`; tests and fallback development can use `InMemoryOperationsStore`.

Elasticsearch is used to demonstrate persistent operational state and real incident search. On startup, the Elasticsearch store seeds demo data only when the Aegis indices are empty. Existing Elasticsearch data is not overwritten, so approving a recommendation survives a Spring Boot restart.

Incident search is backed by Elasticsearch for:

```bash
curl 'http://localhost:8080/api/incidents?search=river'
curl 'http://localhost:8080/api/incidents?severity=critical'
curl 'http://localhost:8080/api/incidents?search=river&severity=critical&kind=flood'
```

The `search` query matches incident `title`, `location`, and `description` text fields. Enum filters such as `severity`, `kind`, and `status` are exact keyword filters, and supplied filters combine with logical AND.

Recommendation approval updates several Elasticsearch documents: recommendation status, incident assignment/status, resource availability, and dashboard `lastUpdated`. Elasticsearch does not provide relational multi-document transactions, so this portfolio implementation keeps the sequence simple and documents that a failure halfway through could leave partial updates. A future production version would need stronger consistency controls.
