# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

- **Build the project**: `./gradlew build`
- **Run the application**: `./gradlew bootRun`
- **Run tests**: `./gradlew test`
- **Run a single test**: `./gradlew test --tests "com.app.flashcard.FlashcardApplicationTests"`
- **Clean build**: `./gradlew clean build`
- **Generate JAR**: `./gradlew bootJar`

## Database Setup

- **Start MySQL database**: `docker-compose up -d`
- **Stop database**: `docker-compose down`
- The application connects to a MySQL database running on localhost:3306
- Database name: `flashcard`
- Root password is empty (development setup)

## Project Architecture

### Core Structure
This is a Spring Boot 3.4.4 web application using Java 21 with a traditional MVC architecture:

- **Package structure**: `com.app.flashcard`
- **Main application**: `FlashcardApplication.java` - Standard Spring Boot entry point
- **Single controller**: `HomeController.java` - Handles all web endpoints and business logic
- **Session management**: Uses `@SessionAttributes("Ses")` with custom `Session.java` class

### Data Layer
- **JPA/Hibernate** with MySQL database
- **Models**: `User`, `Card`, `Deck`, `LearningLog` - JPA entities with standard CRUD
- **Repositories**: Spring Data JPA repositories for each model
- **Database**: Configured for auto-creation (`spring.jpa.hibernate.ddl-auto=create`)

### Web Layer
- **Template engine**: Thymeleaf for server-side rendering
- **Static resources**: CSS and JavaScript in both `src/main/resources/static/` and `src/main/webapp/`
- **Views**: JSP templates in `src/main/webapp/views/` and `src/main/resources/templates/`
- **Forms**: Separate form classes for data binding (`CardForm`, `DeckForm`, `LoginForm`, `RegistForm`)

### Key Features
- **User authentication**: Session-based login/logout system
- **Flashcard management**: Create decks, add cards, spaced repetition learning
- **Learning algorithm**: Cards have status and remind_time for spaced repetition
- **Progress tracking**: `LearningLog` tracks daily learning sessions

### Important Notes
- The application uses session-based authentication (not Spring Security)
- All business logic is centralized in `HomeController.java`
- Database schema is recreated on each startup (development mode)
- Duplicate static resources exist in both webapp/ and resources/ directories