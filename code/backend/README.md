# Backend

This folder will contain the **DevFácil** REST API, built with Node.js and Express.

## Planned Structure

```
backend/
├── src/
│   ├── controllers/      # Route handlers
│   ├── services/         # Business logic
│   ├── repositories/     # Database access layer
│   ├── models/           # Data models / Prisma schema
│   ├── middlewares/      # Auth, validation, error handling
│   ├── routes/           # Express router definitions
│   └── app.ts            # Express app setup
├── tests/                # Postman / Insomnia collection exports
├── prisma/
│   └── schema.prisma     # Database schema
├── .env.example          # Environment variables template
├── package.json
└── tsconfig.json
```

## Endpoints

| Method | Route                           | Description                    |
|--------|---------------------------------|--------------------------------|
| POST   | `/api/solicitacoes`             | Create a new service request   |
| GET    | `/api/solicitacoes`             | List all service requests      |
| GET    | `/api/solicitacoes/:id`         | Get a service request by ID    |
| PATCH  | `/api/solicitacoes/:id/status`  | Update service request status  |

## Getting Started

```bash
# Install dependencies
npm install

# Copy environment variables
cp .env.example .env

# Run database migrations
npx prisma migrate dev

# Start development server
npm run dev
```
