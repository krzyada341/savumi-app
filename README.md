# Savumi

> AI-powered personal recipe manager — save, organize, and discover recipes
> with smart import from social media.

## Status

🚧 **In Development** — Week 1 of 12

## What is Savumi?

Savumi is a portfolio project demonstrating modern full-stack engineering
practices. It's a personal recipe manager with AI-powered features:

- **Smart Import** — paste a TikTok/Instagram/blog link, AI extracts the
  recipe automatically
- **Cooking Assistant** — chat with AI while cooking ("what can replace
  cream?", "how long to boil eggs?")
- **Smart Suggestions** — get recipe ideas based on what's in your fridge

## Tech Stack

**Backend:** Java 21, Spring Boot 3.3, Spring Security (JWT), PostgreSQL,
Flyway, Spring AI

**Frontend:** React 18, TypeScript, TanStack Router/Query, Tailwind CSS,
shadcn/ui

**Infrastructure:** Docker, docker-compose, GitHub Actions

## Local Development

### Prerequisites

- Java 21
- Docker & Docker Compose
- Node.js 20+ (for frontend, when added)

### Setup

\`\`\`bash

# Start PostgreSQL

docker-compose up -d

# Run backend

cd backend
./mvnw spring-boot:run
\`\`\`

Backend available at `http://localhost:8080`.

## Architecture Decisions

See [`docs/decisions/`](docs/decisions/) for Architecture Decision Records (ADRs).

## License

MIT
