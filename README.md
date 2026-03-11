# Cinema Checkout

Aplicacion FullStack para un flujo de compra de cine construida con React, microservicios Spring Boot, PostgreSQL y PayU Sandbox.

Demo publica:

- `https://retocineplanet.web.app/`

## Quick Start

Para ejecutar el proyecto localmente:

```bash
docker compose up --build
```

Luego abre:

- Frontend: `http://localhost:5173`
- Swagger Gateway: `http://localhost:8080/swagger-ui.html`

Para la evaluacion local no es necesario configurar variables adicionales.

Notas:

- El flujo completo puede probarse con ingreso como `Invitado`
- `PayU Sandbox` ya usa credenciales de prueba
- El login con Google puede requerir que `http://localhost:5173` este autorizado en Google Cloud

## Stack

- Frontend: `React + Vite`
- Backend: `Spring Boot`
- Base de datos: `PostgreSQL`
- Autenticacion: `Google Identity Services + JWT`
- Pagos: `PayU Sandbox`
- Infraestructura local: `Docker Compose`

## Arquitectura

- `frontend`
  - interfaz web
- `backend/premieres-service`
  - catalogo de peliculas
- `backend/candystore-service`
  - catalogo de dulceria
- `backend/gateway-service`
  - autenticacion y orquestacion del flujo
- `backend/complete-service`
  - cierre de compra y persistencia final
- `db/init`
  - script de inicializacion de PostgreSQL

## Alcance

- Flujo completo `Home -> Login -> Dulceria -> Pago -> Compra correcta`
- Login con Google e ingreso como invitado
- Consumo de catalogos REST
- Checkout con integracion minima a `PayU Sandbox`
- Registro final de compra en PostgreSQL mediante funcion almacenada
- Swagger habilitado en los microservicios

## Testing

El proyecto incluye `17 tests`:

- Frontend: `1`
- `premieres-service`: `1`
- `candystore-service`: `1`
- `complete-service`: `1`
- `gateway-service`: `13`

### Frontend

```bash
cd frontend
npm run test
```

### Backend

```bash
docker run --rm -v %cd%\backend\premieres-service:/app -w /app maven:3.9.9-eclipse-temurin-17 mvn test
docker run --rm -v %cd%\backend\candystore-service:/app -w /app maven:3.9.9-eclipse-temurin-17 mvn test
docker run --rm -v %cd%\backend\complete-service:/app -w /app maven:3.9.9-eclipse-temurin-17 mvn test
docker run --rm -v %cd%\backend\gateway-service:/app -w /app maven:3.9.9-eclipse-temurin-17 mvn test
```

## Despliegue

La solucion fue preparada para ejecutarse tanto en local como en despliegue usando variables de entorno.

Configuraciones tipicas:

- Frontend en Firebase Hosting
- Microservicios backend en Render o Railway
- PostgreSQL gestionado

Variables relevantes de despliegue:

- Frontend: `VITE_GATEWAY_API`, `VITE_GOOGLE_CLIENT_ID`
- Gateway: `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `CORS_ALLOWED_ORIGIN_PATTERNS`, `SERVICES_*`, `PAYU_*`
- Complete Service: `SPRING_DATASOURCE_*`, `CORS_ALLOWED_ORIGIN_PATTERNS`

## API Docs

Swagger esta disponible en cada microservicio mediante:

- `GET /swagger-ui.html`
