# API Documentation

## Base Information

- **Base URL**: `http://localhost:8080/api/v1`
- **Authentication**: JWT Bearer Token (stateless)
- **Content-Type**: `application/json`
- **API Version**: v1
- **Architecture**: Pure REST API (legacy web controllers removed)

## Authentication

All API endpoints except authentication endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <jwt-token>
```

### Response Format

All API responses follow a standard format:

```json
{
  "success": boolean,
  "message": string,
  "data": T | null,
  "error": string | null,
  "timestamp": [year, month, day, hour, minute, second, nanosecond]
}
```

## Authentication Endpoints

### Register User

**POST** `/auth/register`

Create a new user account.

**Request Body:**
```json
{
  "loginId": "string",     // required, max 50 chars
  "password": "string",    // required, min 6 chars
  "name": "string",        // required, max 100 chars
  "age": number,           // optional, 1-150
  "email": "string"        // optional, valid email format
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "loginId": "testuser",
    "name": "Test User",
    "age": 25,
    "email": "test@example.com"
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 30, 24, 253671509]
}
```

**Error Response (409):**
```json
{
  "success": false,
  "message": "User already exists",
  "data": null,
  "error": "Login ID is already taken",
  "timestamp": [2025, 8, 3, 19, 30, 24, 253671509]
}
```

**Validation Errors (400):**
- Login ID is required
- Password must be between 6 and 255 characters
- Name is required
- Age must be between 1 and 150
- Email should be valid

### Login User

**POST** `/auth/login`

Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "loginId": "string",     // required
  "password": "string"     // required
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "loginId": "testuser",
      "name": "Test User",
      "age": 25,
      "email": "test@example.com"
    }
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 30, 29, 68950604]
}
```

**Error Response (401):**
```json
{
  "success": false,
  "message": "Invalid credentials",
  "data": null,
  "error": "Login failed",
  "timestamp": [2025, 8, 3, 19, 30, 29, 68950604]
}
```

## Deck Management

### Get User Decks

**GET** `/decks`

Retrieve all decks for the authenticated user.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "name": "Spanish Vocabulary",
      "newCardNum": 5,
      "learningCardNum": 3,
      "dueCardNum": 2
    },
    {
      "id": 2,
      "userId": 1,
      "name": "Math Formulas",
      "newCardNum": 10,
      "learningCardNum": 0,
      "dueCardNum": 0
    }
  ],
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Get Deck by ID

**GET** `/decks/{deckId}`

Retrieve a specific deck by its ID.

**Parameters:**
- `deckId` (path): Integer - The deck ID

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 1,
    "userId": 1,
    "name": "Spanish Vocabulary",
    "newCardNum": 5,
    "learningCardNum": 3,
    "dueCardNum": 2
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

**Error Response (404):**
```json
{
  "success": false,
  "message": "Deck not found",
  "data": null,
  "error": "Deck does not exist or access denied",
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Create Deck

**POST** `/decks`

Create a new deck for the authenticated user.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "deckName": "string"     // required, max 100 chars
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Deck created successfully",
  "data": {
    "id": 3,
    "userId": 1,
    "name": "New Deck",
    "newCardNum": 0,
    "learningCardNum": 0,
    "dueCardNum": 0
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Update Deck

**PUT** `/decks/{deckId}`

Update an existing deck.

**Parameters:**
- `deckId` (path): Integer - The deck ID

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "deckName": "string"     // required, max 100 chars
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Deck updated successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "name": "Updated Deck Name",
    "newCardNum": 5,
    "learningCardNum": 3,
    "dueCardNum": 2
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Delete Deck

**DELETE** `/decks/{deckId}`

Delete an existing deck and all its cards.

**Parameters:**
- `deckId` (path): Integer - The deck ID

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Deck deleted successfully",
  "data": null,
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

## Card Management

### Get Cards by Deck

**GET** `/cards/deck/{deckId}`

Retrieve all cards in a specific deck.

**Parameters:**
- `deckId` (path): Integer - The deck ID

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "deckId": 1,
      "frontContent": "What is 'hello' in Spanish?",
      "backContent": "Hola",
      "status": 0,
      "remindTime": "2025-08-04"
    },
    {
      "id": 2,
      "deckId": 1,
      "frontContent": "What is 'goodbye' in Spanish?",
      "backContent": "Adiós",
      "status": 1,
      "remindTime": "2025-08-05"
    }
  ],
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Get Card by ID

**GET** `/cards/{cardId}`

Retrieve a specific card by its ID.

**Parameters:**
- `cardId` (path): Integer - The card ID

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 1,
    "deckId": 1,
    "frontContent": "What is 'hello' in Spanish?",
    "backContent": "Hola",
    "status": 0,
    "remindTime": "2025-08-04"
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Create Card

**POST** `/cards`

Create a new card in a deck.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "deckId": number,        // required
  "frontContent": "string", // required
  "backContent": "string"   // required
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Card created successfully",
  "data": {
    "id": 3,
    "deckId": 1,
    "frontContent": "What is 'thank you' in Spanish?",
    "backContent": "Gracias",
    "status": 0,
    "remindTime": "2025-08-04"
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Update Card

**PUT** `/cards/{cardId}`

Update an existing card.

**Parameters:**
- `cardId` (path): Integer - The card ID

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "deckId": number,        // required
  "frontContent": "string", // required
  "backContent": "string"   // required
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Card updated successfully",
  "data": {
    "id": 1,
    "deckId": 1,
    "frontContent": "Updated front content",
    "backContent": "Updated back content",
    "status": 0,
    "remindTime": "2025-08-04"
  },
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

### Delete Card

**DELETE** `/cards/{cardId}`

Delete an existing card.

**Parameters:**
- `cardId` (path): Integer - The card ID

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Card deleted successfully",
  "data": null,
  "error": null,
  "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
}
```

## Data Models

### User
```json
{
  "id": number,
  "loginId": "string",
  "name": "string",
  "age": number,
  "email": "string"
}
```

### Deck
```json
{
  "id": number,
  "userId": number,
  "name": "string",
  "newCardNum": number,
  "learningCardNum": number,
  "dueCardNum": number
}
```

### Card
```json
{
  "id": number,
  "deckId": number,
  "frontContent": "string",
  "backContent": "string",
  "status": number,        // 0: new, 1: learning, 2: due
  "remindTime": "string"   // ISO date format (YYYY-MM-DD)
}
```

### LoginResponse
```json
{
  "token": "string",
  "tokenType": "Bearer",
  "user": User
}
```

## Error Codes

### HTTP Status Codes

- **200 OK** - Successful GET, PUT, DELETE operations
- **201 Created** - Successful POST operations
- **400 Bad Request** - Validation errors, malformed requests
- **401 Unauthorized** - Invalid or missing authentication
- **403 Forbidden** - Insufficient permissions
- **404 Not Found** - Resource not found
- **409 Conflict** - Resource already exists (e.g., duplicate login ID)
- **500 Internal Server Error** - Server-side errors

### Common Error Messages

**Authentication Errors:**
- "Invalid credentials" - Wrong username/password
- "Token expired" - JWT token has expired
- "Access denied" - Insufficient permissions

**Validation Errors:**
- "Login ID is required"
- "Password must be between 6 and 255 characters"
- "Email should be valid"
- "Deck name is required"

**Resource Errors:**
- "User already exists"
- "Deck not found"
- "Card not found"
- "Deck does not exist or access denied"

## Rate Limiting

Currently, no rate limiting is implemented. Future versions may include:

- Authentication endpoints: 5 requests per minute
- API endpoints: 100 requests per minute per user
- Burst allowance: 10 additional requests

## Versioning

The API uses URL versioning:
- Current version: `/api/v1/`
- Future versions: `/api/v2/`, `/api/v3/`, etc.

Backward compatibility will be maintained for at least one major version.

## Examples

### Complete Authentication Flow

```bash
# 1. Register a new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "johnsmith",
    "password": "securepassword123",
    "name": "John Smith",
    "age": 28,
    "email": "john@example.com"
  }'

# 2. Login to get JWT token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "johnsmith",
    "password": "securepassword123"
  }'

# Response will include token:
# {"success":true,"data":{"token":"eyJhbGciOiJIUzUxMiJ9...","user":{...}}}

# 3. Use token for authenticated requests
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# 4. Create a deck
curl -X POST http://localhost:8080/api/v1/decks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "deckName": "Spanish Vocabulary"
  }'

# 5. Get all decks
curl -X GET http://localhost:8080/api/v1/decks \
  -H "Authorization: Bearer $TOKEN"

# 6. Create a card
curl -X POST http://localhost:8080/api/v1/cards \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "deckId": 1,
    "frontContent": "What is hello in Spanish?",
    "backContent": "Hola"
  }'
```

### Error Handling Example

```bash
# Attempt to access protected resource without token
curl -X GET http://localhost:8080/api/v1/decks

# Response:
# HTTP 401 Unauthorized
# {
#   "success": false,
#   "message": "Access denied",
#   "data": null,
#   "error": "Authentication required",
#   "timestamp": [2025, 8, 3, 19, 35, 15, 123456789]
# }
```

## OpenAPI/Swagger Documentation

Interactive API documentation is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

The Swagger UI provides:
- Interactive API testing
- Request/response examples
- Schema documentation
- Authentication testing

---

For additional support or questions about the API, please refer to the main README.md or create an issue in the project repository.