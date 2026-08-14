# Mini Banking System

Full-stack banking demo focused on transaction safety, idempotent transfers, and a clear separation between domain rules and delivery adapters.

The project uses Java 17, Spring Boot, JPA, H2, Angular, and a hexagonal architecture. It is a synthetic portfolio project: the data resets when the demo API restarts and no real financial data is involved.

## Deployment

The repository includes a Vercel configuration for the Angular app and a Render blueprint for the Spring Boot API. After connecting the repository in each provider, set the frontend project root to `frontend` and configure `CORS_ALLOWED_ORIGINS` on Render with the exact Vercel origin.

Render's free web service may take up to a minute to wake after inactivity; the frontend should surface that cold-start state to demo users.

## Architecture

```text
Angular dashboard
        |
        v
REST controllers -> application ports -> domain rules
        |                                      |
        +-------------- JPA adapter ----------+
                              |
                              v
                         H2 database
```

The backend keeps domain and application code independent from Spring MVC and JPA. The Angular client calls versioned REST endpoints and sends an idempotency key for every transfer.

## API

```http
GET /actuator/health
GET /api/v1/accounts/1001
GET /api/v1/accounts/1001/statement
POST /api/v1/transfers
Idempotency-Key: transfer-demo-001
Content-Type: application/json

{
  "sourceAccountId": 1001,
  "targetAccountId": 1002,
  "amount": 50.00
}
```

Repeated requests with the same key and payload return the original receipt. Reusing a key with a different payload returns a conflict. Transfers validate account existence, positive amounts, same-account rules, sufficient balance, and atomic persistence.

## Run locally

Prerequisites: Java 17+, Maven 3.8+, Node.js 20+, and npm 10+.

```sh
cd backend
mvn spring-boot:run
```

In another terminal:

```sh
cd frontend
npm ci
npm start
```

Open `http://localhost:4200`. The local API runs on `http://localhost:8080` and the seeded demo accounts are `1001`, `1002`, and `1003`.

## Tests

```sh
cd backend && mvn test
cd frontend && npm test
cd frontend && npm run build
```

GitHub Actions runs backend tests, frontend tests/build, and the backend Docker build on every push and pull request.

The H2 database is intentionally ephemeral for this demo.

## License

MIT
