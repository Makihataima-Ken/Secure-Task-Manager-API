# Secure Task Manager API

A secure REST API built with Spring Boot and integrated with Keycloak for authentication and authorization.

---

## 📌 A. Keycloak Setup Instructions

### 1. Start Keycloak
You can run Keycloak in a container:

```bash
docker run -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:24.0.2 start-dev
```
Keycloak admin console will be accessible at:
```http://localhost:8080/admin```
### 2. Configure Realm, Client, and User
#### Option A: Manual Setup
1. Realm: ``` task-manager ```
2. Client: ```task-api-client```
   - Access Type: confidential
   - Enable Direct Access Grants
   - Set valid redirect URIs (e.g., http://localhost:8081/*)
3. User: Create a test user
   - Set password
   - Disable Temporary
   - Make sure no required actions are pending (like update password)
#### Option B: Import Realm
Import an existing realm JSON file:
1. Log in to the admin console.
2. Navigate to Realm Settings → Import.
3. Upload secure-task-realm.json from the keycloak-config/ folder in this repo.

## 🚀 B. Build and Run Instructions (Spring Boot)
Prerequisites
 - Java 21
 - Maven 3.9+
 - (Optional) PostgreSQL or other database

1. Build the Project
   ```bash
   mvn clean install
   ```
2. Run the Application
   ```bash
   mvn spring-boot:run
   ```
Application will run on:
```http://localhost:8081```

## 🔐 C. Get a JWT Token from Keycloak
POST Request to Token Endpoint

Endpoint:
```ruby
POST http://localhost:8080/realms/secure-task-realm/protocol/openid-connect/token
```
Headers:
```bash
Content-Type: application/x-www-form-urlencoded
```
Body (x-www-form-urlencoded):
```vbnet
grant_type: password
client_id: secure-task-client
username: testuser
password: yourPassword
client_secret: <only if client is confidential>
```
Sample cURL
```bash
curl -X POST http://localhost:8080/realms/secure-task-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=secure-task-client" \
  -d "username=testuser" \
  -d "password=yourPassword"
```
## 📬 D. Testing API Endpoints
Authorization Header (in Postman or cURL):
```http
Authorization: Bearer <access_token>
```
Example Endpoints
1. Create Task
```bash
curl -X POST http://localhost:8081/tasks \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"title": "My Task", "description": "Test task"}'
```
2. Get All Tasks (for authenticated user)
```bash
curl -X GET http://localhost:8081/tasks \
  -H "Authorization: Bearer <your_token>"
```
3. Update Task
```bash
curl -X PUT http://localhost:8081/tasks/1 \
  -H "Authorization: Bearer <your_token>" \
  -H "Content-Type: application/json" \
  -d '{"title": "Updated Title", "description": "Updated Description"}'
```
## 📘 E. Design Decisions & Justifications
### i. Authorization Logic: "User Owns Task"
1. Implemented in the Service Layer by checking the user ID from the JWT against the task owner.
2. Why Service Layer?
 - Offers better control and clearer exception handling.
 - Easier to debug and write unit tests than ```@PreAuthorize```.
 - ```@PreAuthorize``` with SpEL can be fragile or tightly coupled to method parameters.
### ii. Keycloak Integration & JWT Handling
- JWT validation is done by Spring Security, using spring-boot-starter-oauth2-resource-server.
- Configuration in application.properties:
  ```properties
  spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/task-manager
  ```
- The Keycloak user ID is extracted from the token’s sub claim:
```java
String userId = jwt.getSubject();
```
### iii. JPA/Hibernate Design Decisions
 - Fetch Types: Lazy loading is used by default to avoid unnecessary data loading.
 - Cascade Types: Minimal use (e.g., CascadeType.PERSIST) to avoid accidental deletions or unintended cascades.
 - Foreign Key: The Task entity includes:
```java
@Column(name = "user_id")
private String userId;
```
This links the task to the Keycloak user by ID (not a local user entity).

### iv. Development Challenges & Resolutions
 - Issue: “Account is not fully set up” error from Keycloak
   Fix: Ensured test users had no required actions and had a permanent password.

 - Issue: 401 errors despite disabling security
   Fix: Ensured Spring Security was properly configured for token validation via Keycloak's issuer URI.

 - Issue: Mapping Keycloak users to tasks
   Fix: Stored Keycloak user ID (sub claim) directly in task records instead of syncing users into the local DB.
## 🛠️ Technologies Used
 - Spring Boot 3+
 - Keycloak 24
 - Java 21
 - Spring Security (OAuth2 Resource Server)
 - JPA + Hibernate
 - PostgreSQL (optional)

## 📂 Project Structure
```bash
src/
├── main/
│   ├── java/com/securetaskmanagerapi/
│   │   ├── controller/          # REST controllers
│   │   ├── entity/               # JPA entities
│   │   ├── repository/          # Spring Data Repositories
│   │   ├── service/             # Business logic and authorization checks
│   │   ├── exception/           # exception logic
│   │   └── config/              # Security & Keycloak config
│   └── resources/
│       └── application.properties      # Keycloak & app configuration
```
