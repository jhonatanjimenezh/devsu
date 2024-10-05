---

# **WS-Account and WS-Customer Microservices Project**

## **General Overview**

This project consists of two main microservices: `WS-Account` and `WS-Customer`. These microservices are designed to manage customer accounts, transactions, and client information in a banking context. Both services are developed with **Spring Boot 3.3.1** and **Java 17**, implementing **Hexagonal Architecture (Clean Architecture)** to maintain separation of concerns, ensuring scalability and maintainability.

The system utilizes **PostgreSQL** for persistence, **RabbitMQ** for asynchronous messaging, and is fully containerized with **Docker** for ease of deployment. These services can communicate with each other and handle both synchronous and asynchronous operations.

## **Key Features**

- **Microservices**: Each service exposes REST APIs to manage accounts, transactions, and clients.
- **Hexagonal Architecture**: Separation between the domain logic and infrastructure, making the services easily testable and modifiable.
- **RabbitMQ Messaging**: Asynchronous communication using **RabbitMQ** for sending and receiving messages.
- **PostgreSQL**: Using **Spring Data JPA** for database operations.
- **Dockerized**: Each service is containerized and managed using Docker Compose.
- **Unit and Integration Testing**: Extensive tests written using **JUnit 5** and **Mockito** for reliability.
- **Swagger**: API documentation using **SpringDoc OpenAPI**.

## **Architecture Overview**

The project adheres to **Hexagonal Architecture** where the core business logic is encapsulated within the domain and application layers, and infrastructure concerns such as databases, messaging systems, and HTTP APIs are handled by adapters.

### **Domain Layer**
- The domain layer contains the core business logic, including entities like `Account`, `Transaction`, `Client`, and `Person`.
- Each entity has its respective repository interface and service for performing operations.

### **Application Layer**
- Use cases, such as `CreateAccountUseCase`, `CreateClientUseCase`, are defined in this layer.
- These use cases define the business operations that can be performed and how they interact with the domain layer.

### **Adapter Layer**
- **Inward Adapters**: REST Controllers like `AccountController` and `ClientController` that handle incoming HTTP requests.
- **Outward Adapters**: Repositories and messaging services like `RabbitMQ` adapters and `Postgres Repositories`, which handle persistence and communication outside the system.

## **System Requirements**

- **Java**: Version 17
- **Gradle**: Version 7+
- **Docker**: Latest version
- **RabbitMQ**: Docker container for RabbitMQ
- **PostgreSQL**: Docker container for PostgreSQL

## **Project Structure**

### **Directories**

```bash
├── database                  # SQL scripts for database setup
├── docker-compose.yml         # Docker Compose configuration for running the system
├── ws-account                 # Account management microservice
└── ws-customer                # Customer management microservice
```

### **ws-account**
- **Controller**: Manages HTTP endpoints for account creation, updates, and transactions.
- **Postgres**: Handles persistence for accounts and transactions using JPA repositories.
- **RabbitMQ**: Contains messaging services for sending and receiving messages related to accounts.

### **ws-customer**
- **Controller**: Manages HTTP endpoints for managing customer and client data.
- **Postgres**: Contains repositories for interacting with customer data stored in PostgreSQL.
- **RabbitMQ**: Messaging adapter to send and receive customer-related messages.

## **Setup and Deployment**

1. **Database Setup**: The SQL scripts to create the necessary tables for both services are located in the `database` folder.
    - `accountdb.sql` for `WS-Account`.
    - `customerdb.sql` for `WS-Customer`.

2. **Docker Setup**:
   The services can be run in containers using Docker Compose. Use the following commands:

   ```bash
   docker-compose up --build
   ```

   This will spin up the following containers:
   - `ws-account` running on port `8081`
   - `ws-customer` running on port `8080`
   - `PostgreSQL` running on port `5432`
   - `RabbitMQ` running on port `5672`

   RabbitMQ management interface can be accessed at `http://localhost:15672` (default login: guest/guest).

3. **Accessing the APIs**:
   - `WS-Account`: `http://localhost:8081/api/accounts`
   - `WS-Customer`: `http://localhost:8080/api/clients`

## **Configuration**

### **PostgreSQL**
Both services use PostgreSQL as the database. The connection details are stored in the `application.yml` for each service.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customerdb
    username: postgres
    password: postgres
```

### **RabbitMQ**
RabbitMQ is used for asynchronous messaging between services. The configuration is handled in `RabbitMQConfig.java`.

```yaml
rabbitmq:
  host: localhost
  port: 5672
  username: guest
  password: guest
```

### **.env File**
Environment variables for the services can be stored in a `.env` file:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/customerdb
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
```

## **Running Tests**

The project includes unit and integration tests that can be executed using Gradle.

```bash
./gradlew test
```

## **API Documentation**

Both services are equipped with Swagger for API documentation. To access the API documentation, navigate to:
- `WS-Account`: `http://localhost:8081/swagger-ui.html`
- `WS-Customer`: `http://localhost:8080/swagger-ui.html`

## **Endpoints Overview**

### **WS-Account Endpoints**
- **GET /accounts**: Retrieve all accounts.
- **POST /accounts**: Create a new account.
- **PUT /accounts/{id}**: Update an existing account.
- **DELETE /accounts/{id}**: Delete an account by ID.

### **WS-Customer Endpoints**
- **GET /clients**: Retrieve all clients.
- **POST /clients**: Create a new client.
- **PUT /clients/{id}**: Update a client.
- **DELETE /clients/{id}**: Delete a client by ID.

## **Exception Handling**

Both services have a global exception handler that catches and formats errors using `GlobalExceptionHandler.java`. This ensures consistent error messages across all endpoints.


