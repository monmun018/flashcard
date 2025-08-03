# Deployment Guide

This guide covers different deployment strategies for the Flashcard Application, from local development to production environments.

## Quick Start (Docker Compose)

For immediate deployment with all services:

```bash
# Clone and start
git clone <repository-url>
cd flashcard
docker-compose up -d

# Access application
open http://localhost:3000
```

## Development Deployment

### Prerequisites

- **Docker & Docker Compose** - For containerized services
- **Java 21** - For local backend development
- **Node.js 18+** - For local frontend development
- **Git** - For source code management

### Local Development Setup

**1. Clone Repository:**
```bash
git clone <repository-url>
cd flashcard
```

**2. Environment Setup:**
```bash
# Copy environment template (if exists)
cp .env.example .env

# Or create environment file
cat > .env << EOF
DATABASE_HOST=localhost
JWT_SECRET=your-secret-key-here
EOF
```

**3. Database Setup:**
```bash
# Start PostgreSQL container
docker-compose up -d db

# Verify database connection
docker exec flashcard-postgres psql -U flashcard_user -d flashcard -c "\l"
```

**4. Backend Development:**
```bash
# Build and run backend
./gradlew bootRun

# Or run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Verify backend is running
curl http://localhost:8080/actuator/health
```

**5. Frontend Development:**
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Access frontend
open http://localhost:3000
```

### Development Workflow

**Backend Development:**
```bash
# Run tests
./gradlew test

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build

# Generate JAR
./gradlew bootJar
```

**Frontend Development:**
```bash
cd frontend

# Type checking
npm run type-check

# Linting
npm run lint

# Build for production
npm run build

# Preview production build
npm run preview
```

## Docker Deployment

### Full Stack with Docker Compose

**1. Production Build:**
```bash
# Build all services
docker-compose build

# Or force rebuild
docker-compose build --no-cache
```

**2. Environment Configuration:**

Create `docker-compose.override.yml` for custom settings:
```yaml
version: '3.8'
services:
  backend:
    environment:
      - JWT_SECRET=your-production-secret
      - SPRING_PROFILES_ACTIVE=production
  
  frontend:
    environment:
      - VITE_API_BASE_URL=https://your-domain.com/api/v1
  
  db:
    environment:
      - POSTGRES_PASSWORD=secure-production-password
    volumes:
      - ./backups:/backups  # For database backups
```

**3. Start Services:**
```bash
# Start all services in background
docker-compose up -d

# View logs
docker-compose logs -f

# Check service status
docker-compose ps
```

**4. Health Checks:**
```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend accessibility
curl -I http://localhost:3000

# Database connectivity
docker exec flashcard-postgres pg_isready -U flashcard_user
```

### Individual Service Deployment

**Backend Only:**
```bash
# Build backend image
docker build -f Dockerfile.backend -t flashcard-backend .

# Run with external database
docker run -d \
  --name flashcard-backend \
  -p 8080:8080 \
  -e DATABASE_HOST=your-db-host \
  -e SPRING_PROFILES_ACTIVE=production \
  flashcard-backend
```

**Frontend Only:**
```bash
# Build frontend image
docker build -f Dockerfile.frontend -t flashcard-frontend .

# Run with external API
docker run -d \
  --name flashcard-frontend \
  -p 3000:3000 \
  -e VITE_API_BASE_URL=https://api.yourdomain.com/api/v1 \
  flashcard-frontend
```

## Production Deployment

### Cloud Platform Deployment

#### AWS Deployment

**Using ECS (Elastic Container Service):**

1. **Push images to ECR:**
```bash
# Login to ECR
aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-west-2.amazonaws.com

# Tag and push images
docker tag flashcard-backend <account-id>.dkr.ecr.us-west-2.amazonaws.com/flashcard-backend:latest
docker push <account-id>.dkr.ecr.us-west-2.amazonaws.com/flashcard-backend:latest

docker tag flashcard-frontend <account-id>.dkr.ecr.us-west-2.amazonaws.com/flashcard-frontend:latest
docker push <account-id>.dkr.ecr.us-west-2.amazonaws.com/flashcard-frontend:latest
```

2. **ECS Task Definition:**
```json
{
  "family": "flashcard-app",
  "cpu": "512",
  "memory": "1024",
  "networkMode": "awsvpc",
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "<account-id>.dkr.ecr.us-west-2.amazonaws.com/flashcard-backend:latest",
      "portMappings": [{"containerPort": 8080}],
      "environment": [
        {"name": "DATABASE_HOST", "value": "your-rds-endpoint"},
        {"name": "SPRING_PROFILES_ACTIVE", "value": "production"}
      ]
    },
    {
      "name": "frontend",
      "image": "<account-id>.dkr.ecr.us-west-2.amazonaws.com/flashcard-frontend:latest",
      "portMappings": [{"containerPort": 3000}]
    }
  ]
}
```

**Using Lambda (Serverless):**
- Consider Spring Cloud Function for backend
- Use CloudFront + S3 for frontend static hosting

#### Google Cloud Platform

**Using Cloud Run:**
```bash
# Deploy backend
gcloud run deploy flashcard-backend \
  --image gcr.io/PROJECT-ID/flashcard-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated

# Deploy frontend
gcloud run deploy flashcard-frontend \
  --image gcr.io/PROJECT-ID/flashcard-frontend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

#### Azure Container Instances

```bash
# Create resource group
az group create --name flashcard-rg --location eastus

# Deploy backend
az container create \
  --resource-group flashcard-rg \
  --name flashcard-backend \
  --image flashcard-backend:latest \
  --ports 8080 \
  --environment-variables DATABASE_HOST=your-db-host

# Deploy frontend
az container create \
  --resource-group flashcard-rg \
  --name flashcard-frontend \
  --image flashcard-frontend:latest \
  --ports 3000
```

### Self-Hosted Production

#### Using Docker Compose on VPS

**1. Server Setup:**
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

**2. Production Configuration:**

Create `docker-compose.prod.yml`:
```yaml
version: '3.8'
services:
  db:
    image: postgres:15-alpine
    restart: always
    environment:
      POSTGRES_DB: flashcard
      POSTGRES_USER: flashcard_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db_data:/var/lib/postgresql/data
      - ./backups:/backups
    networks:
      - app-network

  backend:
    build:
      context: .
      dockerfile: Dockerfile.backend
    restart: always
    environment:
      DATABASE_HOST: db
      SPRING_PROFILES_ACTIVE: production
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - db
    networks:
      - app-network

  frontend:
    build:
      context: .
      dockerfile: Dockerfile.frontend
    restart: always
    environment:
      VITE_API_BASE_URL: https://yourdomain.com/api/v1
    networks:
      - app-network

  nginx:
    image: nginx:alpine
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - backend
      - frontend
    networks:
      - app-network

volumes:
  db_data:

networks:
  app-network:
    driver: bridge
```

**3. Nginx Configuration:**

Create `nginx.conf`:
```nginx
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:3000;
    }

    server {
        listen 80;
        server_name yourdomain.com;
        return 301 https://$server_name$request_uri;
    }

    server {
        listen 443 ssl;
        server_name yourdomain.com;

        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;

        location /api/ {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

**4. SSL Certificate Setup:**
```bash
# Using Let's Encrypt with Certbot
sudo apt install certbot
sudo certbot certonly --standalone -d yourdomain.com

# Copy certificates
sudo cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem ./ssl/cert.pem
sudo cp /etc/letsencrypt/live/yourdomain.com/privkey.pem ./ssl/key.pem
```

**5. Deploy:**
```bash
# Set environment variables
export DB_PASSWORD=secure_password
export JWT_SECRET=your_jwt_secret

# Deploy
docker-compose -f docker-compose.prod.yml up -d
```

#### Using Kubernetes

**1. Create Kubernetes Manifests:**

`k8s/namespace.yaml`:
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: flashcard
```

`k8s/database.yaml`:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: flashcard
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        env:
        - name: POSTGRES_DB
          value: flashcard
        - name: POSTGRES_USER
          value: flashcard_user
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: postgres-storage
        persistentVolumeClaim:
          claimName: postgres-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: flashcard
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
```

`k8s/backend.yaml`:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
  namespace: flashcard
spec:
  replicas: 2
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: flashcard-backend:latest
        env:
        - name: DATABASE_HOST
          value: postgres-service
        - name: SPRING_PROFILES_ACTIVE
          value: production
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: app-secret
              key: jwt-secret
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: backend-service
  namespace: flashcard
spec:
  selector:
    app: backend
  ports:
  - port: 8080
    targetPort: 8080
```

**2. Deploy to Kubernetes:**
```bash
# Apply manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n flashcard

# View logs
kubectl logs -f deployment/backend -n flashcard
```

## Database Migration

### Backup and Restore

**Backup Database:**
```bash
# Using Docker
docker exec flashcard-postgres pg_dump -U flashcard_user flashcard > backup_$(date +%Y%m%d_%H%M%S).sql

# Direct PostgreSQL
pg_dump -h localhost -U flashcard_user -d flashcard > backup.sql
```

**Restore Database:**
```bash
# Using Docker
docker exec -i flashcard-postgres psql -U flashcard_user flashcard < backup.sql

# Direct PostgreSQL
psql -h localhost -U flashcard_user -d flashcard < backup.sql
```

### Data Migration

**From MySQL to PostgreSQL:**
```bash
# Export MySQL data
mysqldump -u root flashcard > mysql_export.sql

# Convert to PostgreSQL format (manual conversion required)
# - Change AUTO_INCREMENT to SERIAL
# - Adjust data types (TINYINT to SMALLINT, etc.)
# - Update SQL syntax differences

# Import to PostgreSQL
psql -U flashcard_user flashcard < postgresql_import.sql
```

## Monitoring and Maintenance

### Health Monitoring

**Application Health:**
```bash
# Backend health check
curl -f http://localhost:8080/actuator/health || echo "Backend unhealthy"

# Database connectivity
docker exec flashcard-postgres pg_isready -U flashcard_user || echo "Database unhealthy"

# Frontend accessibility
curl -f http://localhost:3000 || echo "Frontend unhealthy"
```

**Automated Health Checks:**

Create `health-check.sh`:
```bash
#!/bin/bash

# Health check script
check_service() {
    local service=$1
    local url=$2
    
    if curl -f -s "$url" > /dev/null; then
        echo "✅ $service is healthy"
        return 0
    else
        echo "❌ $service is unhealthy"
        return 1
    fi
}

check_service "Backend" "http://localhost:8080/actuator/health"
check_service "Frontend" "http://localhost:3000"

# Check database
if docker exec flashcard-postgres pg_isready -U flashcard_user > /dev/null 2>&1; then
    echo "✅ Database is healthy"
else
    echo "❌ Database is unhealthy"
fi
```

**Cron Job for Regular Checks:**
```bash
# Add to crontab
*/5 * * * * /path/to/health-check.sh >> /var/log/flashcard-health.log 2>&1
```

### Log Management

**Centralized Logging:**
```bash
# View all service logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db

# Log rotation setup
echo '/var/log/flashcard-*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0644 root root
}' | sudo tee /etc/logrotate.d/flashcard
```

### Backup Strategy

**Automated Backup Script:**

Create `backup.sh`:
```bash
#!/bin/bash

BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Database backup
docker exec flashcard-postgres pg_dump -U flashcard_user flashcard > "$BACKUP_DIR/db_backup_$DATE.sql"

# Application data backup (if any)
tar -czf "$BACKUP_DIR/app_data_$DATE.tar.gz" /path/to/app/data

# Cleanup old backups (keep last 30 days)
find "$BACKUP_DIR" -name "*.sql" -mtime +30 -delete
find "$BACKUP_DIR" -name "*.tar.gz" -mtime +30 -delete

echo "Backup completed: $DATE"
```

**Schedule Backups:**
```bash
# Daily backup at 2 AM
0 2 * * * /path/to/backup.sh >> /var/log/backup.log 2>&1
```

## Rollback Procedures

### Application Rollback

**Docker Deployment:**
```bash
# Tag current version
docker tag flashcard-backend:latest flashcard-backend:backup

# Pull previous version
docker pull flashcard-backend:previous

# Stop current containers
docker-compose down

# Update docker-compose.yml with previous image tags
# Restart services
docker-compose up -d
```

**Database Rollback:**
```bash
# Stop application
docker-compose down

# Restore database
docker exec -i flashcard-postgres psql -U flashcard_user flashcard < backup_previous.sql

# Restart application
docker-compose up -d
```

### Emergency Procedures

**Complete System Restore:**
```bash
#!/bin/bash
# emergency-restore.sh

echo "Starting emergency restore..."

# Stop all services
docker-compose down

# Restore database
docker-compose up -d db
sleep 10
docker exec -i flashcard-postgres psql -U flashcard_user flashcard < emergency_backup.sql

# Deploy stable version
git checkout stable
docker-compose up --build -d

echo "Emergency restore completed"
```

## Security Considerations

### Production Security Checklist

- [ ] Change default passwords
- [ ] Use strong JWT secret keys
- [ ] Enable HTTPS/TLS encryption
- [ ] Configure firewall rules
- [ ] Set up regular security updates
- [ ] Implement backup encryption
- [ ] Configure log monitoring
- [ ] Set up intrusion detection
- [ ] Regular security audits
- [ ] Database access restrictions

### Environment Variables Security

**Use Docker Secrets:**
```yaml
services:
  backend:
    secrets:
      - db_password
      - jwt_secret
    environment:
      - DB_PASSWORD_FILE=/run/secrets/db_password
      - JWT_SECRET_FILE=/run/secrets/jwt_secret

secrets:
  db_password:
    file: ./secrets/db_password.txt
  jwt_secret:
    file: ./secrets/jwt_secret.txt
```

**Use External Secret Management:**
- AWS Secrets Manager
- Azure Key Vault
- Google Secret Manager
- HashiCorp Vault

---

This deployment guide covers various scenarios from development to production. Choose the appropriate deployment method based on your infrastructure requirements and scale needs.