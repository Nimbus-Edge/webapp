# Cloud Native Web Application

A high-performance, cloud-native web application built with modern DevOps practices and cloud-first architecture. This Spring Boot application provides robust user management, secure authentication, profile image handling, health monitoring, and automated deployment pipelines. Designed for scalability and resilience, it leverages containerization, infrastructure as code, and automated testing to ensure reliable deployment across cloud environments.

The application follows cloud-native principles including:
- Microservices-based architecture
- Containerized deployment
- Infrastructure as Code (IaC)
- Automated CI/CD pipeline
- Health monitoring and logging
- Stateless application design
- Cloud-native storage with MySQL and S3
- DevOps-oriented development practices

## Table of Contents
- [Cloud Native Web Application](#cloud-native-web-application)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Technology Stack](#technology-stack)
  - [Project Structure](#project-structure)
  - [Prerequisites](#prerequisites)
  - [Setup and Installation](#setup-and-installation)
  - [API Endpoints](#api-endpoints)
    - [Health Check](#health-check)
    - [User Management](#user-management)
    - [Profile Picture Management](#profile-picture-management)
    - [Email Verification](#email-verification)
  - [Testing](#testing)
  - [CI/CD Pipeline](#cicd-pipeline)
    - [Secrets \& Configs (via GitHub Actions Secrets)](#secrets--configs-via-github-actions-secrets)

## Features
- User Management (Create, Read, Update)
- Email Verification System
- Profile Picture Management via S3
- Basic Authentication
- Health Check Endpoints
- Database Integration with MySQL
- S3 Integration for File Storage
- Comprehensive Error Handling
- Detailed Logging System
- Metrics Collection with Actuator
- Input Validation
- CI/CD Integration

## Technology Stack
- **Runtime**: Java 17
- **Framework**: Spring Boot 3
- **Database**: MySQL 8
- **ORM**: Spring Data JPA (Hibernate)
- **Authentication**: Basic Auth / Spring Security
- **Storage**: AWS S3
- **Email**: AWS SNS, SendGrid
- **Testing**: JUnit 5, Mockito
- **Logging**: SLF4J + Logback
- **Metrics**: Spring Boot Actuator
- **Validation**: Hibernate Validator (JSR-380)
- **CI/CD**: GitHub Actions
- **Infrastructure**: Packer, AWS
- **File Upload**: Multipart/form-data (Spring MVC)

## Project Structure
```
src/main/java/com/cloud/webapp/
├── config/           # Spring configuration (security, beans, etc.)
├── controller/       # REST API controllers
├── DTO/              # Data Transfer Objects
├── entity/           # JPA entities (database models)
├── exceptions/       # Custom exception classes
├── mapper/           # DTO-Entity mappers
├── repository/       # Spring Data repositories
├── service/          # Business logic services
├── utils/            # Utility classes
└── WebappApplication.java  # Application entry point
```

## Prerequisites
- Java 17+
- Maven or Gradle
- MySQL 8+
- AWS Account with permissions for S3, SNS
- SendGrid API key

## Setup and Installation

1. Clone the repository:
```bash
git clone https://github.com/kaushik-manivannan-cloud-org/webapp.git
cd webapp
```

2. Set the following environment variables required by `application.properties`:
```env
DB_URL=jdbc:mysql://localhost:3306/your_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

AWS_REGION=us-east-1
AWS_BUCKET_NAME=your-s3-bucket

SENDGRID_API_KEY=your-sendgrid-api-key
SENDGRID_FROM_EMAIL=no-reply@example.com

SNS_TOPIC_NAME=your-sns-topic-name
```

3. Ensure MySQL is running and the database schema is created.

4. Build and run the application:
```bash
# Using Maven
./mvnw spring-boot:run

# Or with Gradle
./gradlew bootRun
```

5. Logs will be written to:
```
/opt/myapp/myapp.log
```

## API Endpoints

### Health Check
- `GET /healthz`  
  - Returns 200 OK if healthy  
  - 503 if database is unavailable  

### User Management
- `POST /v1/user`  
  - Registers new user  
  - Triggers email verification  

- `GET /v1/user/self`  
  - Returns user info  
  - Requires basic auth + verified email  

- `PUT /v1/user/self`  
  - Updates user info  
  - Requires basic auth + verified email  

### Profile Picture Management
- `POST /v1/user/self/pic`  
  - Uploads profile picture to S3  
  - Supports JPG, JPEG, PNG (max 10MB)  

- `GET /v1/user/self/pic`  
  - Downloads profile picture  

- `DELETE /v1/user/self/pic`  
  - Deletes profile picture  

### Email Verification
- `GET /v1/user/verify?token=...`  
  - Verifies email using token (expires in 2 min)

## Testing

Run unit and integration tests:
```bash
./mvnw test
```

## CI/CD Pipeline

The application uses GitHub Actions for CI/CD. The pipeline runs on PR merges to the `main` branch and follows these stages:

1. **Build & Test**
   - Java 17 + MySQL 8
   - Runs JUnit + Mockito test suites

2. **Artifact Creation**
   - Packages Spring Boot JAR + configs
   - Uploads build artifact

3. **Packer Build**
   - Builds an AMI using Ubuntu 24.04 LTS
   - Installs Java, app JAR, systemd service, CloudWatch agent

4. **Auto Scaling Group Update**
   - Updates Launch Template with new AMI
   - Performs rolling update with:
     - 90% minimum healthy instances
     - Zero downtime strategy
   - Verifies deployment success via health checks

### Secrets & Configs (via GitHub Actions Secrets)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `AWS_REGION`, `AWS_BUCKET_NAME`
- `SENDGRID_API_KEY`, `SENDGRID_FROM_EMAIL`
- `SNS_TOPIC_NAME`

The pipeline ensures:
- Safe, verified deployments
- Environment-specific flexibility
- Automated rollback in case of failure