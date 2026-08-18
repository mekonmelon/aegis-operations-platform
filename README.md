# Aegis

Aegis is a real-time crisis operations platform prototype. The React frontend currently runs in demo mode, and the Java backend provides a standalone in-memory REST API for the first backend iteration.

## Run the frontend locally

You need a recent version of [Node.js](https://nodejs.org/) (Node 20 or newer recommended) and npm.

```bash
npm install
npm run dev
```

Open the URL printed by Vite (normally `http://localhost:5173`). To create and inspect a production build:

```bash
npm run build
npm run preview
```

## Run the backend locally

You need Java 21. The backend is a Spring Boot application in `backend/` and runs on port `8080`.

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
│   └── store/              In-memory demo data store
└── src/test/               Spring Boot API tests

src/
├── components/             Reusable dashboard panels and presentation
├── data/mockData.ts        Typed, hardcoded prototype dataset
├── hooks/                  React data-loading hook
├── services/
│   ├── dataSource.ts       UI-facing data source contract
│   └── mockDataSource.ts   Current local implementation
├── types/domain.ts         Incident, resource, facility, and dashboard types
├── App.tsx                 Dashboard composition
├── main.tsx                React entry point
└── styles.css              Responsive command-center visual system
```

## Architecture

Dashboard components receive typed data through props and do not import mock values directly. `OperationsDataSource` is the boundary between the application and its data. The current `MockOperationsDataSource` fulfills that contract from local data; a future API-backed implementation can replace it at the composition point without rewriting the dashboard components.

The map is intentionally a self-contained CSS visualization. Its incident and facility markers are populated from the same typed dashboard data and require no map service or network connection.

The backend intentionally uses an in-memory store only. It implements the documented REST contract in `docs/api-contract.md` and does not include a database, authentication, Elasticsearch, Spark, Docker, or frontend integration yet.
