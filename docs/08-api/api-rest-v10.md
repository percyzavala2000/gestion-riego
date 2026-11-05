# API REST v10.0

Base URL: `/api/v1`. La versión inicial reutiliza la sesión web de Spring Security; JWT y cuentas del agricultor se incorporarán en v10.1.

## Recursos
- `GET /api/v1/agricultores?q=&page=0`
- `GET /api/v1/agricultores/{id}`
- `POST /api/v1/agricultores`
- `PUT /api/v1/agricultores/{id}`
- `PATCH /api/v1/agricultores/{id}/estado`
- `DELETE /api/v1/agricultores/{id}`
- `GET /api/v1/parcelas`
- `GET /api/v1/parcelas/{id}`
- `GET /api/v1/turnos`
- `GET /api/v1/turnos/{id}`
- `POST /api/v1/turnos/{id}/iniciar`
- `POST /api/v1/turnos/{id}/completar`
- `POST /api/v1/turnos/{id}/cancelar`

## Seguridad
Administrador y operador acceden a esta API operativa. Tesorería no accede. OpenAPI y Swagger están restringidos al administrador. Esta versión no habilita CORS general ni autenticación por token.

## Errores
La API devuelve `application/problem+json` con `status`, `title`, `detail`, `instance` y `correlationId`. Validación produce 400, ausencia 404 y conflicto de negocio 409.

## Documentación interactiva
- `/v3/api-docs`
- `/swagger-ui.html`
