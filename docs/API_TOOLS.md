# REST API Tools Documentation

## Overview

This document describes the reusable REST API tools added to the Swagger Petstore project. These tools provide authentication, validation, and a client library for interacting with the Petstore API.

## Architecture

```
io.swagger.petstore
├── client/              # Reusable REST API client
│   ├── PetStoreClient.java
│   ├── ApiClientConfig.java
│   └── ApiResponse.java
├── security/            # Authentication & authorization
│   ├── ApiKeyAuthFilter.java
│   ├── ApiKeyStore.java
│   └── SecurityContext.java
└── validation/          # Request/response validation
    ├── RequestValidator.java
    └── ValidationResult.java
```

---

## 1. REST API Client (`io.swagger.petstore.client`)

### Quick Start

```java
// Simple creation
PetStoreClient client = new PetStoreClient("http://localhost:8080/api/v3", "special-key");

// Builder pattern
ApiClientConfig config = ApiClientConfig.builder()
    .baseUrl("http://localhost:8080/api/v3")
    .apiKey("special-key")
    .connectTimeout(5000)
    .readTimeout(10000)
    .build();
PetStoreClient client = new PetStoreClient(config);
```

### Pet Operations

```java
// Get pet by ID
ApiResponse<Pet> response = client.getPetById(1L, Pet.class);
if (response.isSuccess()) {
    Pet pet = response.getBody();
}

// Find pets by status
ApiResponse<String> pets = client.findPetsByStatus("available");

// Find pets by tags
ApiResponse<String> pets = client.findPetsByTags(Arrays.asList("tag1", "tag2"));

// Add a new pet
Pet newPet = new Pet();
newPet.setId(100L);
newPet.setName("Buddy");
newPet.setStatus("available");
ApiResponse<String> response = client.addPet(newPet);

// Update existing pet
pet.setName("New Name");
ApiResponse<String> response = client.updatePet(pet);

// Delete pet
ApiResponse<String> response = client.deletePet(1L);
```

### Store Operations

```java
// Get inventory
ApiResponse<String> inventory = client.getInventory();

// Place order
Order order = new Order();
order.setId(10L);
order.setPetId(1L);
order.setQuantity(2);
order.setStatus("placed");
ApiResponse<String> response = client.placeOrder(order);

// Get order by ID
ApiResponse<Order> order = client.getOrderById(10L, Order.class);

// Delete order
ApiResponse<String> response = client.deleteOrder(10L);
```

### User Operations

```java
// Create user
User user = new User();
user.setUsername("john_doe");
user.setEmail("john@example.com");
ApiResponse<String> response = client.createUser(user);

// Login
ApiResponse<String> session = client.loginUser("john_doe", "password123");

// Get user
ApiResponse<User> user = client.getUserByName("john_doe", User.class);

// Update user
user.setEmail("newemail@example.com");
ApiResponse<String> response = client.updateUser("john_doe", user);

// Delete user
ApiResponse<String> response = client.deleteUser("john_doe");

// Logout
ApiResponse<String> response = client.logoutUser();
```

### Response Handling

```java
ApiResponse<Pet> response = client.getPetById(1L, Pet.class);

// Check status
response.isSuccess();      // 2xx
response.isClientError();  // 4xx
response.isServerError();  // 5xx

// Get data
response.getStatusCode();  // HTTP status code
response.getBody();        // Parsed response object
response.getRawBody();     // Raw JSON string
```

---

## 2. Authentication & Authorization (`io.swagger.petstore.security`)

### API Key Authentication Filter

The `ApiKeyAuthFilter` is a servlet filter that validates API keys on protected endpoints.

**Supported authentication methods:**
- `api_key` header: `api_key: special-key`
- Authorization Bearer: `Authorization: Bearer special-key`
- Query parameter: `?api_key=special-key`

**Public endpoints (no auth required):**
- `GET /api/v3/openapi.json`
- `GET /api/v3/openapi.yaml`
- `GET /api/v3/user/login`
- `GET /api/v3/user/logout`
- `OPTIONS` requests (CORS preflight)

### Registering the Filter

Add to `web.xml`:
```xml
<filter>
    <filter-name>ApiKeyAuthFilter</filter-name>
    <filter-class>io.swagger.petstore.security.ApiKeyAuthFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>ApiKeyAuthFilter</filter-name>
    <url-pattern>/api/v3/*</url-pattern>
</filter-mapping>
```

### Managing API Keys

```java
ApiKeyStore store = ApiKeyStore.getInstance();

// Add a new valid key
store.addKey("new-api-key");

// Remove a key
store.removeKey("revoked-key");

// Validate a key
boolean valid = store.isValidKey("some-key");
```

### Security Context

```java
SecurityContext ctx = new SecurityContext("special-key", "read:pets", "write:pets");

ctx.hasScope("read:pets");   // true
ctx.hasReadAccess();         // true
ctx.hasWriteAccess();        // true
```

---

## 3. Request Validation (`io.swagger.petstore.validation`)

### Validating Pets

```java
Pet pet = new Pet();
pet.setName("Buddy");
pet.setPhotoUrls(Arrays.asList("http://example.com/photo.jpg"));
pet.setStatus("available");

ValidationResult result = RequestValidator.validatePet(pet);
if (!result.isValid()) {
    String errors = result.getErrorMessage();  // semicolon-separated errors
    List<String> errorList = result.getErrors();
}
```

**Pet validation rules:**
- `name` is required (max 100 chars)
- `photoUrls` must have at least one entry
- `status` must be: `available`, `pending`, or `sold`

### Validating Orders

```java
Order order = new Order();
order.setPetId(1L);
order.setQuantity(5);
order.setStatus("placed");

ValidationResult result = RequestValidator.validateOrder(order);
```

**Order validation rules:**
- `petId` must be a positive number
- `quantity` must be greater than 0
- `status` must be: `placed`, `approved`, or `delivered`

### Validating Users

```java
User user = new User();
user.setUsername("john_doe");
user.setEmail("john@example.com");
user.setPhone("123-456-7890");

ValidationResult result = RequestValidator.validateUser(user);
```

**User validation rules:**
- `username` is required (max 50 chars)
- `email` must match basic email format (if provided)
- `phone` must contain only digits, hyphens, plus, parens, spaces (if provided)

### Utility Validators

```java
// Validate pet status parameter
ValidationResult result = RequestValidator.validatePetStatus("available,pending");

// Validate ID fields
ValidationResult result = RequestValidator.validateId(1L, "petId");
```

---

## 4. Example curl Requests

### Pet Endpoints

```bash
# Get pet by ID
curl -X GET "http://localhost:8080/api/v3/pet/1" \
  -H "api_key: special-key" \
  -H "Accept: application/json"

# Response:
# {"id":1,"category":{"id":2,"name":"Cats"},"name":"Cat 1","photoUrls":["url1","url2"],"tags":[{"id":1,"name":"tag1"},{"id":2,"name":"tag2"}],"status":"available"}

# Find pets by status
curl -X GET "http://localhost:8080/api/v3/pet/findByStatus?status=available" \
  -H "api_key: special-key" \
  -H "Accept: application/json"

# Add a new pet
curl -X POST "http://localhost:8080/api/v3/pet" \
  -H "api_key: special-key" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 100,
    "name": "Buddy",
    "category": {"id": 1, "name": "Dogs"},
    "photoUrls": ["http://example.com/buddy.jpg"],
    "tags": [{"id": 1, "name": "friendly"}],
    "status": "available"
  }'

# Update a pet
curl -X PUT "http://localhost:8080/api/v3/pet" \
  -H "api_key: special-key" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Updated Cat",
    "status": "sold"
  }'

# Delete a pet
curl -X DELETE "http://localhost:8080/api/v3/pet/1" \
  -H "api_key: special-key"
```

### Store Endpoints

```bash
# Get inventory
curl -X GET "http://localhost:8080/api/v3/store/inventory" \
  -H "api_key: special-key"

# Response:
# {"placed":100,"approved":50,"delivered":50}

# Place an order
curl -X POST "http://localhost:8080/api/v3/store/order" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 10,
    "petId": 1,
    "quantity": 2,
    "shipDate": "2024-01-15T00:00:00.000Z",
    "status": "placed",
    "complete": false
  }'

# Get order by ID
curl -X GET "http://localhost:8080/api/v3/store/order/1"

# Delete order
curl -X DELETE "http://localhost:8080/api/v3/store/order/1"
```

### User Endpoints

```bash
# Login
curl -X GET "http://localhost:8080/api/v3/user/login?username=user1&password=test"

# Response:
# "Logged in user session: 1234567890"

# Create user
curl -X POST "http://localhost:8080/api/v3/user" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 100,
    "username": "new_user",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "secret123",
    "phone": "555-0100",
    "userStatus": 1
  }'

# Get user by name
curl -X GET "http://localhost:8080/api/v3/user/user1" \
  -H "api_key: special-key"

# Update user
curl -X PUT "http://localhost:8080/api/v3/user/user1" \
  -H "api_key: special-key" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "username": "user1",
    "firstName": "Updated",
    "lastName": "Name",
    "email": "updated@example.com"
  }'

# Delete user
curl -X DELETE "http://localhost:8080/api/v3/user/user1" \
  -H "api_key: special-key"

# Logout
curl -X GET "http://localhost:8080/api/v3/user/logout"
```

---

## 5. Running Tests

```bash
mvn test
```

Test classes:
- `PetControllerTest` - Pet CRUD operations (13 tests)
- `OrderControllerTest` - Order operations (8 tests)
- `UserControllerTest` - User operations (11 tests)
- `RequestValidatorTest` - Validation logic (20 tests)
- `PetStoreClientTest` - Client configuration (8 tests)
- `ApiKeyStoreTest` - API key management (5 tests)

---

## 6. Error Response Format

All error responses follow a consistent format:

```json
{
  "code": 401,
  "type": "error",
  "message": "Missing API key. Provide via 'api_key' header or 'Authorization: Bearer <key>'"
}
```

Common error codes:
| Code | Meaning |
|------|---------|
| 400  | Bad Request - Invalid input or missing required fields |
| 401  | Unauthorized - Missing API key |
| 403  | Forbidden - Invalid API key |
| 404  | Not Found - Resource doesn't exist |
| 422  | Unprocessable Entity - Validation failed |

---

## Assumptions

1. **In-memory storage**: The data layer uses in-memory collections. Data resets on server restart.
2. **API Key authentication**: Default valid keys are `special-key` and `test-api-key`. In production, replace `ApiKeyStore` with a database-backed implementation.
3. **OAuth2 scopes**: The OpenAPI spec defines `petstore_auth` OAuth2 flows. The current implementation uses API key auth as a simplified alternative. The `SecurityContext` class models scopes for future OAuth2 integration.
4. **Validation is advisory**: The `RequestValidator` utility is provided for use in controllers but is not automatically enforced via the filter chain to maintain backward compatibility with the existing swagger-inflector routing.
