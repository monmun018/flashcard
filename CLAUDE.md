# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 📚 Documentation

Comprehensive documentation is available in the `/docs` directory:

- **README.md** - Main project overview and quick start guide
- **docs/ARCHITECTURE.md** - Technical architecture and system design
- **docs/API.md** - Complete REST API documentation
- **docs/DEPLOYMENT.md** - Deployment guides for all environments

## 🚀 Quick Access URLs

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Database**: postgresql://localhost:5432/flashcard

## Development Commands

### Backend (Spring Boot)
- **Build the project**: `./gradlew build` (from /backend directory)
- **Run the application**: `./gradlew bootRun`
- **Run tests**: `./gradlew test`
- **Run a single test**: `./gradlew test --tests "com.app.flashcard.FlashcardApplicationTests"`
- **Run specific test package**: `./gradlew test --tests "com.app.flashcard.card.controller.*"`
- **Clean build**: `./gradlew clean build`
- **Generate JAR**: `./gradlew bootJar`
- **Check dependencies**: `./gradlew dependencies`

### Frontend (React + TypeScript)
- **Install dependencies**: `npm install` (from /frontend directory)
- **Start dev server**: `npm run dev`
- **Build for production**: `npm run build`
- **Lint code**: `npm run lint`
- **Preview production build**: `npm run preview`

### Full Stack Development
- **Start all services**: `docker-compose up -d` (from /docker directory)
- **Stop all services**: `docker-compose down`
- **Rebuild and start**: `docker-compose up --build -d`
- **View logs**: `docker-compose logs [service_name]`

## Database Setup

- **Start PostgreSQL database**: `docker-compose up -d db` (from /docker directory)
- **Stop database**: `docker-compose down`
- **View database logs**: `docker logs flashcard-postgres`
- **Connect to database**: `docker exec -it flashcard-postgres psql -U flashcard_user flashcard`
- The application connects to a PostgreSQL database running on localhost:5432
- Database name: `flashcard`
- Username: `flashcard_user`, Password: `flashcard_pass`
- Database schema uses Flyway migrations in `src/main/resources/db/migration/`

## Project Architecture

### Overview
This is a modern full-stack application with a **React SPA frontend** and **Spring Boot REST API backend**, transitioning from a monolithic Thymeleaf web app to a microservice-ready architecture.

### Core Structure
- **Frontend**: React 19 + TypeScript + Vite serving modern SPA at port 3000
- **Backend**: Spring Boot 3.4.4 with Java 21 providing REST APIs at port 8080
- **Database**: PostgreSQL 15 with Flyway migrations for schema management
- **Containerization**: Docker Compose orchestrating all services with proper networking

### Dual Architecture Pattern
The application currently supports **both web and API architectures**:

1. **Legacy Web Controllers** (`/card/controller/`, `/deck/controller/`, etc.) - Thymeleaf-based MVC
2. **Modern API Controllers** (`/api/v1/`) - REST endpoints for React frontend

This allows gradual migration from server-rendered to client-rendered architecture.

### Backend Feature Modules
Each backend feature follows Domain-Driven Design with clear separation of concerns:

- **User Module** (`user/`): Authentication, registration, profile management
- **Card Module** (`card/`): Individual flashcard CRUD operations  
- **Deck Module** (`deck/`): Flashcard collection management
- **Learning Module** (`learning/`): Spaced repetition algorithm and session tracking

**Layer Structure per Feature:**
- `controller/` - Web MVC controllers (Thymeleaf views)
- `model/` - JPA entities with database mapping
- `repository/` - Spring Data JPA repositories
- `service/` - Business logic and domain operations
- `dto/` - Data transfer objects for API boundaries
- `form/` - Form binding objects for web controllers

### Frontend Feature Architecture
The React frontend uses a **feature-based modular structure**:

```
frontend/src/features/
├── auth/           # Authentication & registration
├── cards/          # Card management
├── decks/          # Deck management  
└── learning/       # Study sessions & progress
```

**Each feature contains:**
- `components/` - React components specific to the feature
- `hooks/` - Custom hooks for state and API interactions
- `services/` - API service functions using Axios
- `types/` - TypeScript definitions

### API Architecture
**Dual API Support:**
- **REST API** (`/api/v1/`): Modern JSON APIs for React frontend
- **Web Controllers** (`/`): Traditional form-based endpoints for Thymeleaf

**API Response Format:**
```json
{
  "success": boolean,
  "message": string,
  "data": T | null,
  "error": string | null,
  "timestamp": [2025, 8, 3, 19, 30, 24, 253671509]
}
```

### Technology Stack
**Frontend:**
- React 19 + TypeScript for type-safe UI development
- Vite 7 for fast development and builds
- TailwindCSS + Headless UI for styling
- TanStack Query for server state management
- Zustand for client state management
- React Hook Form + Zod for form validation

**Backend:**
- Spring Boot 3.4.4 with Java 21
- Spring Security 6 with JWT authentication
- Spring Data JPA with PostgreSQL
- Flyway for database migrations
- OpenAPI 3 for API documentation
- MapStruct for DTO mapping (configured but not yet implemented)

### Key Architectural Patterns
- **Feature-based organization**: Both frontend and backend organized by business domains
- **Clean Architecture**: Clear separation between API, business logic, and data layers
- **Authentication**: Stateless JWT-based auth supporting both web sessions and API tokens
- **Database**: PostgreSQL with Flyway migrations for version control
- **Validation**: Multi-layer validation (client-side Zod, server-side Bean Validation)
- **Error Handling**: Centralized exception handling with consistent API responses

## Development Workflow

### Local Development Setup
1. **Prerequisites**: Java 21, Node.js 18+, Docker & Docker Compose
2. **Database**: `cd docker && docker-compose up -d db`
3. **Backend**: `cd backend && ./gradlew bootRun`
4. **Frontend**: `cd frontend && npm install && npm run dev`

### Full Containerized Development
```bash
cd docker
docker-compose up --build -d
```

### Development Environment Configuration
- **Java version**: 21 (required)
- **Node.js version**: 18+ (for frontend)
- **Spring Boot version**: 3.4.4
- **React version**: 19.1.0
- **Build tools**: Gradle (backend), Vite (frontend)
- **Database**: PostgreSQL 15 (containerized)

### Testing Strategy
**Backend Testing:**
- **Location**: `backend/src/test/java/com/app/flashcard/`
- **Framework**: JUnit 5 + Spring Boot Test + Mockito
- **Structure**: Organized by feature modules (card, deck, learning, user)
- **Types**: Unit tests for services, integration tests for controllers
- **Main test**: `FlashcardApplicationTests.java` - Application context loading

**Frontend Testing:**
- **Framework**: Vitest (configured but tests not yet implemented)
- **Strategy**: Component testing with React Testing Library
- **Structure**: Tests co-located with features

### Important Development Notes
- **Database Schema**: Managed by Flyway migrations in `backend/src/main/resources/db/migration/` (development uses create-drop for rapid iteration)
- **API Documentation**: Available at http://localhost:8080/swagger-ui.html when backend is running
- **Hot Reload**: Both frontend (Vite HMR) and backend (Spring DevTools) support hot reloading
- **CORS**: Backend configured to allow frontend origin (localhost:3000)
- **Logging**: Backend logs to `backend/logs/flashcard-app.log`
- **Security**: JWT tokens for API authentication (pure REST API architecture - legacy web controllers removed)
- **Build Tools**: Gradle for backend (Java 21 required), Vite for frontend (Node.js 18+)
- **Authentication Testing**: Use provided test credentials or register via `/api/v1/auth/register`