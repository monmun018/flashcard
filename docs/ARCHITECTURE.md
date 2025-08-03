# Technical Architecture Documentation

## Overview

The Flashcard Application follows a modern, containerized microservice architecture with a React SPA frontend and Spring Boot REST API backend, designed for scalability and maintainability.

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React SPA     │    │  Spring Boot    │    │   PostgreSQL    │
│   (Frontend)    │◄──►│    (Backend)    │◄──►│   (Database)    │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
    ┌─────────────────────────────────────────────────────────┐
    │                Docker Network                           │
    │              flashcard-network                          │
    └─────────────────────────────────────────────────────────┘
```

## Frontend Architecture (React SPA)

### Technology Stack
- **React 19** - Latest React with concurrent features
- **TypeScript** - Type safety and developer experience
- **Vite 7** - Fast build tool and dev server
- **Tailwind CSS 3** - Utility-first CSS framework
- **TanStack Query 5** - Server state management
- **Zustand** - Client state management
- **React Router 7** - Client-side routing
- **React Hook Form** - Form management
- **Zod** - Schema validation

### Folder Structure
```
frontend/src/
├── components/          # Reusable UI components
│   ├── ui/             # Base components (Button, Input, Card)
│   ├── forms/          # Form-specific components
│   └── layout/         # Layout components
├── features/           # Feature-based modules
│   ├── auth/           # Authentication feature
│   │   ├── components/ # Auth-specific components
│   │   ├── hooks/      # Custom hooks for auth
│   │   ├── services/   # Auth API services
│   │   └── types/      # Auth type definitions
│   ├── cards/          # Card management feature
│   ├── decks/          # Deck management feature
│   └── learning/       # Learning session feature
├── pages/              # Route page components
├── shared/             # Shared utilities and configurations
│   ├── api/            # API client and configuration
│   ├── hooks/          # Reusable custom hooks
│   ├── store/          # Global state management
│   ├── types/          # Global type definitions
│   └── utils/          # Utility functions
└── main.tsx            # Application entry point
```

### State Management Architecture

**Global State (Zustand):**
```typescript
// Authentication state
interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}
```

**Server State (TanStack Query):**
- Handles all API requests and caching
- Automatic refetching and synchronization
- Optimistic updates for better UX

### Component Architecture

**Base Components:**
- `Button` - Configurable button with variants
- `Input` - Form input with validation display
- `Card` - Container component for content cards

**Feature Components:**
- Self-contained within feature modules
- Use feature-specific hooks and services
- Follow container/presenter pattern

### Routing Strategy

**Public Routes:**
- `/login` - User authentication
- `/register` - User registration

**Protected Routes:**
- `/dashboard` - Main application dashboard
- `/decks/:id` - Deck detail view
- `/study/:deckId` - Learning session view

**Route Protection:**
```typescript
const ProtectedRoute = ({ children }) => {
  const isAuthenticated = useAuthStore(state => state.isAuthenticated);
  return isAuthenticated ? children : <Navigate to="/login" />;
};
```

## Backend Architecture (Spring Boot)

### Technology Stack
- **Spring Boot 3.4.4** - Main framework
- **Java 21** - LTS Java version
- **Spring Security 6** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Primary database
- **JWT** - Stateless authentication
- **OpenAPI 3** - API documentation
- **Docker** - Containerization

### Package Structure
```
com.app.flashcard/
├── api/                    # REST API layer
│   ├── v1/                # API version 1 controllers
│   │   ├── AuthController
│   │   ├── CardApiController
│   │   └── DeckApiController
│   └── dto/               # Data Transfer Objects
│       ├── request/       # Request DTOs
│       └── response/      # Response DTOs
├── card/                  # Card domain
│   ├── controller/        # Web controllers
│   ├── service/          # Business logic
│   ├── model/            # JPA entities
│   ├── repository/       # Data access
│   └── dto/              # Domain DTOs
├── deck/                  # Deck domain
├── learning/              # Learning domain
├── user/                  # User domain
├── config/                # Spring configuration
│   ├── SecurityConfig
│   └── WebConfig
└── shared/               # Shared components
    ├── security/         # Security utilities
    ├── exception/        # Exception handling
    └── validation/       # Custom validators
```

### Layered Architecture

**1. API Layer (Controllers):**
```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request);
}
```

**2. Service Layer (Business Logic):**
```java
@Service
public class UserService {
    public User save(User user);
    public User findByUserLoginID(String loginId);
    public boolean existsByUserLoginID(String loginId);
}
```

**3. Repository Layer (Data Access):**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserLoginID(String userLoginID);
    boolean existsByUserLoginID(String userLoginID);
}
```

**4. Model Layer (Entities):**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private int userID;
}
```

### Security Architecture

**JWT Authentication Flow:**
```
1. User login → Validate credentials
2. Generate JWT token → Include user claims
3. Return token → Frontend stores in localStorage
4. Subsequent requests → Include token in Authorization header
5. JWT filter → Validate token and set authentication context
```

**Security Configuration:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### Database Design

**Entity Relationships:**
```
User (1) ←→ (N) Deck (1) ←→ (N) Card
  │                              │
  └─────────→ (N) LearningLog ←──┘
```

**Key Tables:**
- `users` - User accounts and authentication
- `deck` - Card collections with metadata
- `card` - Individual flashcards with content
- `learningLog` - Learning session tracking

**Schema Considerations:**
- Uses quoted identifiers for PostgreSQL compatibility
- `create-drop` DDL for development (will migrate to Flyway)
- Foreign key constraints for data integrity

## Data Flow Architecture

### Frontend → Backend Communication

**1. Authentication Flow:**
```
LoginForm → useAuth hook → authService → POST /api/v1/auth/login → JWT token → localStorage
```

**2. Data Fetching Flow:**
```
Component → TanStack Query → apiClient → GET /api/v1/decks → Response → Component update
```

**3. Data Mutation Flow:**
```
Form → useMutation → apiClient → POST /api/v1/decks → Success → Query invalidation → Refetch
```

### API Response Format

**Standard Response Wrapper:**
```json
{
  "success": boolean,
  "message": string,
  "data": T | null,
  "error": string | null,
  "timestamp": [2025, 8, 3, 19, 30, 24, 253671509]
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "error": "Login ID is required",
  "timestamp": [2025, 8, 3, 19, 30, 24, 253671509]
}
```

## Development Architecture

### Development Workflow

**1. Local Development:**
```bash
# Database
docker-compose up -d db

# Backend
./gradlew bootRun

# Frontend
cd frontend && npm run dev
```

**2. Full Docker Development:**
```bash
docker-compose up --build -d
```

### Build Pipeline

**Frontend Build Process:**
```
TypeScript compilation → Vite bundling → Static assets → Docker image
```

**Backend Build Process:**
```
Gradle compilation → JAR generation → Docker image → Container
```

### Environment Configuration

**Development:**
- Frontend: Vite dev server with HMR
- Backend: Spring Boot DevTools
- Database: Docker PostgreSQL

**Production (Docker):**
- Frontend: Nginx serving static files
- Backend: Standalone JAR in container
- Database: PostgreSQL container with volume persistence

## Performance Architecture

### Frontend Performance

**Code Splitting:**
- Route-based code splitting with React.lazy
- Feature-based module organization
- Dynamic imports for large dependencies

**Caching Strategy:**
- TanStack Query caching for API responses
- Browser caching for static assets
- Service worker for offline capabilities (planned)

**Bundle Optimization:**
- Vite's automatic chunking
- Tree shaking for unused code
- Asset optimization (images, fonts)

### Backend Performance

**Database Optimization:**
- Indexed queries on frequent lookups
- Connection pooling with HikariCP
- Lazy loading for JPA relationships

**Caching (Planned):**
- Redis for session data
- Application-level caching for frequently accessed data
- CDN for static content delivery

**API Optimization:**
- Pagination for large datasets
- DTO projections to minimize data transfer
- Async processing for heavy operations

## Security Architecture

### Frontend Security

**Authentication:**
- JWT token storage in localStorage
- Automatic token inclusion in API requests
- Token expiration handling with refresh

**Route Protection:**
- Protected route components
- Automatic redirect to login
- Role-based access control (planned)

**Input Validation:**
- Zod schema validation
- XSS prevention through React's built-in escaping
- CSRF protection through JWT stateless design

### Backend Security

**Authentication & Authorization:**
- JWT stateless authentication
- Spring Security integration
- Password encryption with BCrypt

**API Security:**
- CORS configuration for frontend domains
- Request rate limiting (planned)
- Input validation with Bean Validation

**Data Security:**
- SQL injection prevention through JPA
- Sensitive data encryption
- Audit logging (planned)

## Monitoring and Observability

### Logging Architecture

**Frontend Logging:**
- Console logging for development
- Error boundary for React error handling
- API request/response logging

**Backend Logging:**
- SLF4J with Logback configuration
- Structured logging with JSON format
- Log levels: ERROR, WARN, INFO, DEBUG

**Database Logging:**
- SQL query logging (development)
- Connection pool monitoring
- Performance metrics tracking

### Health Checks

**Application Health:**
- Spring Boot Actuator endpoints
- Database connectivity checks
- Custom health indicators

**Container Health:**
- Docker health check commands
- Service dependency validation
- Automatic restart policies

## Deployment Architecture

### Container Strategy

**Multi-stage Docker Builds:**
```dockerfile
# Frontend: Node build → Nginx serve
FROM node:18-alpine AS build
# ... build process
FROM nginx:alpine AS serve

# Backend: Gradle build → Java runtime
FROM openjdk:21-jdk-slim AS build
# ... build process
CMD ["java", "-jar", "app.jar"]
```

**Network Configuration:**
- Docker Compose networking
- Service discovery by container name
- Port mapping for external access

### Scalability Considerations

**Horizontal Scaling:**
- Stateless backend design
- Database connection pooling
- Load balancer ready (nginx/traefik)

**Vertical Scaling:**
- JVM memory optimization
- Database query optimization
- Container resource limits

## Future Architecture Enhancements

### Phase 3: Enhanced Backend Architecture

**CQRS Implementation:**
```
Commands (Write) → Command Handlers → Domain Events → Event Store
Queries (Read) → Query Handlers → Read Models → Optimized Views
```

**Event-Driven Architecture:**
- Domain events for learning progress
- Async event processing
- Event sourcing for audit trails

**Caching Layer:**
- Redis for distributed caching
- Cache-aside pattern implementation
- Cache invalidation strategies

### Phase 4: Advanced Features

**Real-time Updates:**
- WebSocket integration
- Server-sent events for progress updates
- Real-time collaboration features

**Advanced Analytics:**
- Learning analytics engine
- Progress prediction algorithms
- Performance optimization recommendations

**Mobile Support:**
- PWA implementation
- Offline synchronization
- Push notifications

---

This architecture documentation provides a comprehensive overview of the current system design and serves as a guide for future development and scaling decisions.