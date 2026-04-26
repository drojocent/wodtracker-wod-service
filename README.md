# WODTracker WOD Service

Microservicio REST que gestiona entrenamientos (WODs), benchmarks, récords personales y resultados de entrenamientos. Implementa autenticación JWT OAuth2 con Spring Security.

## Características

- CRUD completo de WODs
- WOD del día
- Registro de resultados de entrenamientos
- Gestión de benchmarks
- Seguimiento de récords personales
- Sistema de propuestas de WODs
- Aprobación de propuestas
- Validación de entrada

## Stack

- Spring Boot 3.5.13
- Java 17
- Spring Security 6
- Spring Data JPA
- PostgreSQL 15
- Flyway para migraciones
- Lombok
- SpringDoc OpenAPI (Swagger)
- Maven

## Requisitos

- Java 17+
- Maven 3.8.0+
- PostgreSQL 14+ (para producción)
- Docker (opcional)

## Instalación

### Desarrollo local con H2

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

Accesible en http://localhost:8081


## Scripts

```bash
mvn clean install          # Build completo
mvn clean package          # Build sin tests
mvn test                   # Ejecutar tests
mvn test -Dtest=NombreTest # Test específico
mvn spring-boot:run        # Ejecutar aplicación
```

## Estructura

```
src/main/java/com/wodtracker/wodservice/
├── config/          # Configuración Spring
├── controller/      # REST Endpoints
├── service/         # Lógica de negocio
├── repository/      # Acceso a datos (JPA)
├── entity/          # Entidades JPA
├── dto/             # Data Transfer Objects
├── mapper/          # Entity-DTO mapping
├── security/        # Autenticación/Autorización
├── exception/       # Excepciones personalizadas
└── Application.java # Main
```

## Endpoints API

WODs:
```
POST /wods
GET /wods
GET /wods/{id}
GET /wods/today
PUT /wods/{id}
DELETE /wods/{id}
```

Resultados:
```
POST /results
GET /results
GET /results/wod/{wodId}
```

Benchmarks:
```
POST /benchmarks
GET /benchmarks
GET /benchmarks/{id}
POST /benchmarks/{id}/results
```

Récords Personales:
```
GET /personal-records
GET /personal-records/{exercise}
PUT /personal-records/{exercise}
```

Propuestas:
```
POST /proposals
GET /admin/proposals
PUT /admin/proposals/{id}/status
```

## Variables de entorno

```env
# Base de datos
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=woddb
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=password

# JWT
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION_MINUTES=60
```

## Testing

```bash
mvn test
mvn test jacoco:report
```

Tests en `src/test/java/com/wodtracker/wodservice/`

## Documentación API

Swagger UI disponible en:
```
http://localhost:8081/swagger-ui.html
JSON: http://localhost:8081/v3/api-docs
```

## Seguridad

- JWT con firma HS256
- Role-based access control
- CORS configurado
- Stateless authentication
- HTTPS recomendado en producción