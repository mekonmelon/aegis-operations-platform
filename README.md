# Aegis

Aegis is a frontend prototype for a real-time crisis operations platform. It presents a polished, offline-capable command-center dashboard using local mock data only—there is no backend, authentication, database, or external map/API integration.

## Run locally

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

## Project structure

```text
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
