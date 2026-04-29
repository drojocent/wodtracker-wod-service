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

## Instalación

### Desarrollo local con H2

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

Accesible en `http://localhost:8081`

## Scripts

```bash
mvn clean install          # Build completo
mvn clean package          # Build sin tests
mvn test                   # Ejecutar tests
mvn test -Dtest=NombreTest # Test específico
mvn spring-boot:run        # Ejecutar aplicación
```

## Estructura

```text
src/main/java/com/wodtracker/wodservice/
├── config/          # Configuración Spring
├── controller/      # Endpoints REST
├── service/         # Lógica de negocio
├── repository/      # Acceso a datos (JPA)
├── entity/          # Entidades JPA
├── dto/
│   ├── request/     # DTOs de entrada
│   └── response/    # DTOs de salida
├── security/        # Autenticación y autorización
├── exception/       # Excepciones personalizadas
└── WodtrackerWodServiceApplication.java # Main
```

## Endpoints API

WODs:

```text
POST   /wods
GET    /wods
GET    /wods/{id}
GET    /wods/today
PUT    /wods/{id}
DELETE /wods/{id}
```

Resultados:

```text
POST /results
PUT  /results/{id}
GET  /results/user/{userId}
GET  /results/wod/{wodId}
```

Benchmarks:

```text
GET    /benchmarks
GET    /benchmarks/{id}
POST   /benchmarks
PUT    /benchmarks/{id}
DELETE /benchmarks/{id}
POST   /benchmarks/{id}/results
GET    /benchmarks/{id}/results/me
```

Récords personales:

```text
GET  /prs/exercises
GET  /prs/{exercise}/me
POST /prs/{exercise}
GET  /prs/{exercise}/me/history
```

Propuestas:

```text
POST  /proposals
GET   /proposals/pending
PATCH /proposals/{id}/approve
PATCH /proposals/{id}/reject
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

```text
http://localhost:8081/swagger-ui.html
JSON: http://localhost:8081/v3/api-docs
```

## Seguridad

- JWT con firma HS256
- Control de acceso basado en roles
- CORS configurado
- Autenticación stateless
