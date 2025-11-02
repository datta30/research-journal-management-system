# Journal Management System

A comprehensive research journal management platform with submission tracking, peer review workflow, and editorial decisions.

## Tech Stack

### Frontend
- React 18 with Material-UI
- React Router for navigation
- React Query for server state management
- Axios for API calls
- Context API for authentication

### Backend
- Spring Boot 3.5.7
- Spring Security with JWT authentication
- Spring Data JPA with MySQL
- Flyway for database migrations
- Actuator for health monitoring

### Database
- MySQL 8.0

## Quick Start with Docker

### Using Pre-built Images from GHCR

```bash
# Pull images from GitHub Container Registry
docker pull ghcr.io/datta30/journal/backend:latest
docker pull ghcr.io/datta30/journal/frontend:latest

# Run with docker-compose
docker compose up -d
```

Services will be available at:
- **Frontend**: http://localhost:3002
- **Backend API**: http://localhost:8082
- **MySQL**: localhost:3307

## Getting Started

### Prerequisites
- Node.js 20+
- Java 21+
- Maven 3.9+
- MySQL 8.0+ (or Docker)

### Local Development

#### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start all services
docker compose up -d

# Stop all services
docker compose down

# View logs
docker compose logs -f
```

Services will be available at:
- **Frontend**: http://localhost:3002
- **Backend API**: http://localhost:8082
- **MySQL**: localhost:3307

#### Option 2: Manual Setup

**Backend:**

1. Create MySQL database and user:
```sql
CREATE DATABASE journal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'journal_app'@'localhost' IDENTIFIED BY 'journal_pass';
GRANT ALL PRIVILEGES ON journal.* TO 'journal_app'@'localhost';
FLUSH PRIVILEGES;
```

2. Start backend:
```bash
cd backend
mvn spring-boot:run
```

Backend runs on http://localhost:8080

**Frontend:**

1. Install dependencies:
```bash
cd frontend
npm install
```

2. Create `.env` file:
```env
REACT_APP_API_URL=http://localhost:8080
```

3. Start dev server:
```bash
npm start
```

Frontend runs on http://localhost:3000

## Docker Configuration

### Port Mappings
- MySQL: 3307 (host) → 3306 (container)
- Backend: 8082 (host) → 8080 (container)
- Frontend: 3002 (host) → 80 (container)

### Environment Variables

Backend container uses:
- `MYSQL_HOST=mysql`
- `MYSQL_PORT=3306`
- `MYSQL_DATABASE=journal`
- `MYSQL_USER=journal_app`
- `MYSQL_PASSWORD=journal_pass`
- `SERVER_PORT=8080`

### Volumes
- `mysql_data`: Persists MySQL database files

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token

### Papers
- `GET /api/papers` - List papers (role-based)
- `POST /api/papers` - Submit paper (Author)
- `GET /api/papers/{id}` - Get paper details
- `PUT /api/papers/{id}` - Update paper

### Reviews
- `GET /api/reviews` - List assigned reviews (Reviewer)
- `POST /api/reviews/{id}` - Submit review

### Editor Operations
- `POST /api/papers/{id}/assign-reviewers` - Assign reviewers
- `POST /api/papers/{id}/decide` - Record editorial decision

## User Roles
- **AUTHOR**: Submit and manage papers
- **REVIEWER**: Review assigned papers
- **EDITOR**: Manage submissions, assign reviewers, make decisions

## Health Check
- Docker: http://localhost:8082/actuator/health
- Local: http://localhost:8080/actuator/health

## Database Migrations
Flyway automatically runs migrations on startup. Migrations are in `backend/src/main/resources/db/migration/`.

## Development Notes
- Backend uses JWT for stateless authentication
- CORS configured for localhost:3000, localhost:3001
- React app uses protected routes with role-based access
- All timestamps stored in UTC

## Troubleshooting

**Port conflicts:**
- Update `docker-compose.yml` port mappings if needed
- Default MySQL runs on 3306; Docker uses 3307

**Backend won't start:**
- Check MySQL connectivity
- Verify Flyway migrations succeeded: `docker compose logs backend`

**Frontend can't connect:**
- Verify `REACT_APP_API_URL` points to correct backend port
- Check browser console for CORS errors

## CI/CD

The project includes GitHub Actions workflow that automatically:
- Builds Docker images for backend and frontend
- Pushes images to GitHub Container Registry (GHCR)
- Tags images with branch names and commit SHA

### Using GHCR Images

Images are published to:
- Backend: `ghcr.io/datta30/journal/backend:latest`
- Frontend: `ghcr.io/datta30/journal/frontend:latest`

To use pre-built images, update `docker-compose.yml`:

```yaml
services:
  backend:
    image: ghcr.io/datta30/journal/backend:latest
    # ... rest of config

  frontend:
    image: ghcr.io/datta30/journal/frontend:latest
    # ... rest of config
```

## Production Notes
- Change default passwords in `docker-compose.yml`
- Use environment-specific configuration
- Enable HTTPS/TLS
- Configure proper CORS origins
- Set up monitoring and logging
