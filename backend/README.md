# Flashcard Application

A modern flashcard learning application built with Spring Boot backend and React frontend, featuring spaced repetition algorithms and user progress tracking.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development)
- Node.js 18+ (for local frontend development)

### Running with Docker (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd flashcard

# Start all services
docker-compose up -d

# Access the application
open http://localhost:3000/login
```

### Running Locally

```bash
# Start database
docker-compose up -d db

# Start backend
./gradlew bootRun

# Start frontend (in another terminal)
cd frontend
npm install
npm run dev
```

## 📱 Application URLs

- **Frontend**: http://localhost:3000
- **Login**: http://localhost:3000/login
- **Register**: http://localhost:3000/register
- **Dashboard**: http://localhost:3000/dashboard
- **Backend API**: http://localhost:8080/api/v1
- **API Documentation**: http://localhost:8080/swagger-ui.html

## 🏗️ Architecture

### Current Stack

**Backend:**
- Spring Boot 3.4.4
- Java 21
- PostgreSQL 15
- Spring Security with JWT
- Spring Data JPA
- OpenAPI/Swagger documentation

**Frontend:**
- React 19 with TypeScript
- Vite build tool
- Tailwind CSS 3.4
- React Router 7
- TanStack Query for API state management
- Zustand for global state
- React Hook Form with Zod validation

**Infrastructure:**
- Docker & Docker Compose
- PostgreSQL database
- Multi-stage Docker builds

### Project Structure

```
flashcard/
├── src/main/java/com/app/flashcard/    # Backend source
│   ├── api/                            # REST API controllers
│   │   ├── v1/                         # API version 1
│   │   └── dto/                        # Data Transfer Objects
│   ├── card/                           # Card management
│   ├── deck/                           # Deck management
│   ├── learning/                       # Learning sessions & progress
│   ├── user/                           # User management
│   ├── config/                         # Spring configuration
│   └── shared/                         # Shared utilities
├── frontend/                           # React frontend
│   ├── src/
│   │   ├── components/                 # Reusable UI components
│   │   ├── features/                   # Feature modules
│   │   ├── pages/                      # Route pages
│   │   └── shared/                     # Shared utilities
│   └── public/
├── docker-compose.yml                  # Container orchestration
└── Dockerfile.*                        # Container definitions
```

## 🔧 Development

### Backend Development

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "com.app.flashcard.user.service.UserServiceTest"

# Clean build
./gradlew clean build

# Generate JAR
./gradlew bootJar
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Run linting
npm run lint

# Preview production build
npm run preview
```

### Database Management

```bash
# Start PostgreSQL
docker-compose up -d db

# Connect to database
docker exec -it flashcard-postgres psql -U flashcard_user -d flashcard

# View logs
docker logs flashcard-postgres

# Reset database
docker-compose down
docker volume rm flashcard_db_data
docker-compose up -d db
```

## 🔐 Authentication

The application uses JWT-based authentication:

1. **Register**: Create a new account at `/register`
2. **Login**: Authenticate at `/login` to receive JWT token
3. **Protected Routes**: JWT token automatically included in API requests
4. **Token Storage**: Stored in localStorage with automatic refresh

### API Authentication

```bash
# Register user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"loginId": "testuser", "password": "password123", "name": "Test User", "age": 25, "email": "test@example.com"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId": "testuser", "password": "password123"}'

# Use JWT token
curl -X GET http://localhost:8080/api/v1/decks \
  -H "Authorization: Bearer <jwt-token>"
```

## 📚 Core Features

### Card Management
- Create, edit, and delete flashcards
- Front and back content support
- Status tracking (new, learning, due)
- Spaced repetition algorithm

### Deck Organization
- Group cards into themed decks
- Deck statistics (new, learning, due cards)
- Progress tracking per deck

### Learning System
- Spaced repetition scheduling
- Learning progress tracking
- Daily learning statistics
- Adaptive difficulty adjustment

### User System
- User registration and authentication
- Personal progress tracking
- Session management

## 🛠️ API Endpoints

### Authentication
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login

### Decks
- `GET /api/v1/decks` - List user decks
- `POST /api/v1/decks` - Create new deck
- `GET /api/v1/decks/{id}` - Get deck details
- `PUT /api/v1/decks/{id}` - Update deck
- `DELETE /api/v1/decks/{id}` - Delete deck

### Cards
- `GET /api/v1/cards/deck/{deckId}` - List cards in deck
- `POST /api/v1/cards` - Create new card
- `GET /api/v1/cards/{id}` - Get card details
- `PUT /api/v1/cards/{id}` - Update card
- `DELETE /api/v1/cards/{id}` - Delete card

## 🐳 Docker Configuration

### Services

**Database (flashcard-postgres):**
- PostgreSQL 15 Alpine
- Port: 5432
- Volume: `db_data` for persistence

**Backend (flashcard-backend):**
- Java 21 OpenJDK
- Port: 8080
- Auto-connects to database

**Frontend (flashcard-frontend):**
- Node.js 18 Alpine with serve
- Port: 3000
- Proxies API calls to backend

### Environment Variables

```bash
# Database
POSTGRES_DB=flashcard
POSTGRES_USER=flashcard_user
POSTGRES_PASSWORD=flashcard_pass

# Backend
DATABASE_HOST=db
SPRING_PROFILES_ACTIVE=docker

# Frontend
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

### Docker Commands

```bash
# Build and start all services
docker-compose up --build -d

# View logs
docker-compose logs -f [service-name]

# Stop services
docker-compose down

# Remove volumes (resets database)
docker-compose down -v

# Force rebuild
docker-compose build --no-cache
```

## 🔧 Configuration

### Application Properties

**Development (`application.properties`):**
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/flashcard
spring.jpa.hibernate.ddl-auto=create-drop

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000
```

**Docker (`application-docker.properties`):**
```properties
# Database
spring.datasource.url=jdbc:postgresql://db:5432/flashcard
spring.jpa.hibernate.ddl-auto=create-drop
```

### Frontend Configuration

**Vite Config (`vite.config.ts`):**
```typescript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

## 🧪 Testing

### Backend Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "UserServiceTest"

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Frontend Testing

```bash
cd frontend

# Run tests (when implemented)
npm test

# Run e2e tests (when implemented)
npm run test:e2e
```

### API Testing

Use the provided API endpoints to test functionality:

```bash
# Health check
curl http://localhost:8080/actuator/health

# API documentation
curl http://localhost:8080/api-docs
```

## 🚨 Troubleshooting

### Common Issues

**Database Connection Errors:**
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check logs
docker logs flashcard-postgres

# Reset database
docker-compose down && docker-compose up -d db
```

**Frontend Build Errors:**
```bash
# Clear node_modules and reinstall
rm -rf frontend/node_modules frontend/package-lock.json
cd frontend && npm install

# Check for TypeScript errors
npm run build
```

**Backend Startup Issues:**
```bash
# Check Java version
java --version

# Check if port 8080 is available
lsof -i :8080

# View application logs
tail -f logs/flashcard-app.log
```

**Docker Issues:**
```bash
# Clean Docker cache
docker system prune -a

# Remove all containers and start fresh
docker-compose down -v
docker-compose up --build -d
```

### Port Conflicts

If you encounter port conflicts:

```bash
# Find processes using ports
lsof -i :3000  # Frontend
lsof -i :8080  # Backend
lsof -i :5432  # Database

# Kill processes if needed
kill -9 <PID>
```

## 📈 Modernization Roadmap

### Phase 1: API Foundation & Database Migration ✅
- [x] PostgreSQL migration from MySQL
- [x] REST API development alongside existing web controllers
- [x] JWT authentication system
- [x] OpenAPI documentation
- [x] Docker containerization

### Phase 2: React SPA Development ✅
- [x] Modern React 19 + TypeScript + Vite setup
- [x] Zustand for state management
- [x] TanStack Query for API calls
- [x] React Router for navigation
- [x] Tailwind CSS + custom components
- [x] Authentication integration

### Phase 3: Enhanced Backend Architecture (Planned)
- [ ] CQRS implementation
- [ ] Domain events for learning progress
- [ ] Redis caching integration
- [ ] Background job processing
- [ ] Advanced spaced repetition algorithms

### Phase 4: Advanced Features (Planned)
- [ ] Real-time progress updates
- [ ] Advanced analytics dashboard
- [ ] Import/export functionality
- [ ] Mobile-responsive design improvements
- [ ] Offline support with PWA

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow existing code style and conventions
- Add tests for new functionality
- Update documentation as needed
- Ensure Docker builds pass
- Test both frontend and backend changes

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔗 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [Tailwind CSS Documentation](https://tailwindcss.com/)

---

**Last Updated:** 2025-08-03  
**Version:** 2.0.0 (Modernized Architecture)