# Mini Banking System

Full-stack banking demo focused on transaction safety, idempotent transfers, and a clear separation between domain rules and delivery adapters.

The project uses Java 17, Spring Boot, JPA, H2, Angular, and a hexagonal architecture. It is a synthetic portfolio project: the data resets when the demo API restarts and no real financial data is involved.

## Live demo

- Frontend: `https://mini-banking-system-victorcmoraes.vercel.app`
- API health: `https://mini-banking-api-victorcmoraes.onrender.com/actuator/health`

The free API may take up to a minute to wake up after inactivity. Reload the frontend once the health endpoint returns `UP`.

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

## Deployment

The Angular app is deployed through Vercel from the `frontend` directory. The Spring Boot API is deployed as a Docker Web Service on Render. Configure `CORS_ALLOWED_ORIGINS` on the API with the exact Vercel origin; the H2 database is intentionally ephemeral for this demo.

## License

MIT
