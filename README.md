# Cinema Ecommerce Challenge

Solucion de referencia para la prueba FullStack:

- Frontend: React + Vite
- Backend: Spring Boot microservices
- DB: MySQL con procedimiento almacenado
- Infra: Docker Compose

## Estructura

- `frontend`: aplicacion React
- `backend/premieres-service`: catalogo de estrenos
- `backend/candystore-service`: catalogo de dulceria
- `backend/gateway-service`: autenticacion, JWT y orquestacion del flujo
- `backend/complete-service`: cierre de compra
- `db/init`: scripts SQL para tablas y procedimientos

## Alcance implementado

- Flujo web completo: `Home -> Login -> Dulceria -> Pago -> Compra correcta`
- Login con Google Identity Services y validacion real del `id_token` en backend
- Integracion minima real con PayU Sandbox en el servicio principal
- JWT para proteger checkout
- Swagger en los cuatro servicios
- Procedimiento almacenado MySQL para registrar la compra final
- Docker Compose para frontend, APIs y base de datos

## Ejecucion

```bash
docker compose up --build
```

Servicios:

- Frontend: `http://localhost:5173`
- Gateway API: `http://localhost:8080/swagger-ui.html`
- Premieres API: `http://localhost:8081/swagger-ui.html`
- Candystore API: `http://localhost:8082/swagger-ui.html`
- Complete API: `http://localhost:8083/swagger-ui.html`

## Variables utiles

- `VITE_GOOGLE_CLIENT_ID`: client id de Google para el frontend
- `GOOGLE_CLIENT_ID`: client id aceptado por el backend para validar tokens
- `PAYU_*`: credenciales y endpoint sandbox de PayU

Nota:
- En este flujo no se usa `Client secret`; Google Sign-In web con `id_token` solo requiere `Client ID`.

## Pruebas PayU Sandbox

- Tarjetas Peru de prueba: `4907840000000005`, `4634010000000005`, `5491610000000001`, `377753000000009`
- Aprobacion:
  - incluye `APPROVED` en el nombre del tarjetahabiente
  - usa CVV `777` o `7777` para AMEX
  - usa una expiracion con mes menor a `06`, por ejemplo `05/30`
- Rechazo:
  - incluye `REJECTED` en el nombre del tarjetahabiente
  - usa CVV `666` o `6666` para AMEX
  - usa una expiracion con mes mayor a `06`, por ejemplo `07/30`
