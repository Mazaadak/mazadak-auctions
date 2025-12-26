# Auctions Service
## Overview
- The Auctions Service is responsible for managing auction listings, their lifecycle (scheduled, started, active, paused, cancelled, ended), bid placement, watchers, and search/filter capabilities.

- It exposes a REST API, persists auction state, and emits domain events to other services in the Mazadak platform.

- The Auctions Service is the owner of auction and bid state within the platform.

## API Endpoints
- See [Auction Service Wiki Page](https://github.com/Mazaadak/.github/wiki/Auctions-Service) for a detailed breakdown of the service's API endpoints
- Swagger UI available at `http://localhost:18089/swagger-ui/index.html` when running locally

## How to Run
You can run it via [Docker Compose](https://github.com/Mazaadak/mazadak-infrastructure) or [Kubernetes](https://github.com/Mazaadak/mazadak-k8s/)

## Tech Stack
- **Spring Boot 3.5.6** (Java 21) 
- **PostgreSQL**
- **Apache Kafka**
- **Netflix Eureka** - Service Discovery
- **Docker & Kubernetes** - Deployment & Containerization
- **Micrometer, OpenTelemetry, Alloy, Loki, Prometheus, Tempo, Grafana** - Observability
- **OpenAPI/Swagger** - API Documentation

## For Further Information
Refer to [Auctions Service Wiki Page](https://github.com/Mazaadak/.github/wiki/Auctions-Service).