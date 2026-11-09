# Vichayal Riego Web

Sistema web para la gestión operativa, financiera y administrativa de la Comisión de Regantes de Vichayal. La aplicación permite administrar agricultores, parcelas, turnos de riego, cuentas por cobrar, pagos, tesorería, caja, auditoría, reportes, inteligencia basada en reglas, observabilidad y una API REST inicial.

> **Versión documentada:** 10.7.0  
> **Estado:** API REST operativa inicial, login responsive, despliegue Docker, observabilidad con Prometheus y Grafana.  
> **Ubicación institucional:** Vichayal, Piura, Perú.

---

## 1. Objetivos del proyecto

Vichayal Riego Web centraliza procesos que normalmente se ejecutan mediante registros manuales o archivos separados:

- Registro y mantenimiento de agricultores.
- Registro de parcelas y cultivos.
- Programación, inicio, finalización y cancelación de turnos de riego.
- Gestión de conceptos de cobro y cuentas por cobrar.
- Registro, confirmación y anulación de pagos.
- Registro de egresos, devoluciones y cierres de caja.
- Generación de reportes PDF y Excel.
- Auditoría técnica con identificadores de correlación.
- Alertas operativas y financieras basadas en reglas.
- Métricas de rendimiento mediante Prometheus.
- Visualización inicial mediante Grafana.
- API REST versionada para agricultores, parcelas y turnos.

---

## 2. Tecnologías utilizadas

| Capa | Tecnología | Uso |
|---|---|---|
| Lenguaje | Java 21 | Implementación del backend |
| Framework | Spring Boot 4.1.1 | Aplicación web y configuración |
| Web MVC | Spring MVC | Controladores, formularios y vistas |
| Plantillas | Thymeleaf | Interfaz web renderizada en servidor |
| Seguridad | Spring Security | Autenticación, sesiones y autorización por roles |
| Persistencia | Spring Data JPA | Repositorios y acceso a datos |
| ORM | Hibernate ORM | Mapeo objeto-relacional |
| Base de datos | PostgreSQL 18 | Almacenamiento transaccional |
| Migraciones | Flyway | Versionado del esquema V1 a V14 |
| API | REST JSON y ProblemDetail | Integración con clientes futuros |
| Documentación API | Springdoc OpenAPI y Swagger UI | Exploración de endpoints |
| Validación | Jakarta Bean Validation | Validación de DTO y formularios |
| Pruebas | JUnit 6, MockMvc y Mockito | Pruebas unitarias y web |
| Integración | Testcontainers 1.21.4 | PostgreSQL temporal en pruebas |
| Cobertura | JaCoCo 0.8.13 | Informe de cobertura |
| Construcción | Maven | Dependencias, pruebas y empaquetado |
| Contenedores | Docker y Docker Compose | Desarrollo, demo y producción |
| Métricas | Micrometer y Prometheus | Métricas de JVM, HTTP y negocio |
| Paneles | Grafana | Visualización de métricas |
| CI | GitHub Actions | Pruebas, construcción e informes |

---

## 3. Arquitectura general

```text
Navegador o cliente REST
          |
          v
Spring Security
          |
          v
Controladores MVC y API REST
          |
          v
Servicios de dominio
          |
          v
Repositorios Spring Data JPA
          |
          v
PostgreSQL
```

Componentes transversales:

```text
Auditoría
Correlación de solicitudes
Manejo global de errores
Flyway
Prometheus
Grafana
Pruebas automatizadas
```

La aplicación se mantiene como un **monolito modular**. Esta estrategia conserva transacciones simples, reduce complejidad operativa y permite separar posteriormente clientes React, Angular o móviles mediante `/api/v1`.

---

## 4. Módulos funcionales

### 4.1 Agricultores

- Registrar agricultores.
- Actualizar datos personales y operativos.
- Activar o desactivar registros.
- Impedir eliminaciones que violen relaciones del dominio.
- Consultar mediante interfaz web y API REST.

### 4.2 Parcelas

- Registrar código, nombre, extensión, cultivo y estado.
- Asociar cada parcela con un agricultor.
- Consultar parcelas desde la web y la API REST.

### 4.3 Turnos de riego

Estados principales:

```text
PROGRAMADO
EN_CURSO
COMPLETADO
CANCELADO
VENCIDO
```

Operaciones:

- Programar turnos.
- Evitar conflictos horarios.
- Iniciar un turno.
- Completar un turno.
- Cancelar con motivo.
- Actualizar turnos vencidos.

### 4.4 Finanzas

- Conceptos de cobro.
- Cuentas por cobrar.
- Estados pendientes, parciales, pagados y vencidos.
- Vinculación entre pagos y cuentas.
- Reversión de saldos al anular pagos o ejecutar devoluciones.

### 4.5 Tesorería

- Registro de pagos.
- Confirmación de pagos digitales.
- Anulación controlada.
- Aplicación idempotente sobre cuentas por cobrar.
- Correlativos únicos.

### 4.6 Caja

- Registro de egresos.
- Solicitud y ejecución de devoluciones.
- Cierre diario.
- Reapertura controlada.
- Restricción de movimientos en fechas cerradas.

### 4.7 Reportes

- Reportes operativos.
- Reportes financieros.
- Exportación PDF.
- Exportación Excel.
- Filtros por fecha, estado y módulo.

### 4.8 Inteligencia basada en reglas

- Turnos vencidos.
- Concentración de demanda.
- Pagos pendientes.
- Cuentas vencidas.
- Devoluciones pendientes.
- Diferencias de caja.

No utiliza todavía aprendizaje automático. Las alertas son determinísticas y explicables.

### 4.9 Auditoría

Registra:

- Usuario.
- Método HTTP.
- Ruta.
- Código de respuesta.
- Resultado.
- Fecha y hora.
- Dirección IP.
- Identificador de correlación.

### 4.10 Observabilidad

Métricas principales:

```text
http_server_requests_seconds
jvm_memory_used_bytes
process_cpu_usage
hikaricp_connections_active
vichayal_pagos_pendientes
vichayal_deuda_total_soles
vichayal_devoluciones_solicitadas
vichayal_auditoria_eventos_eliminados_total
```

---

## 5. Roles del sistema

| Rol | Alcance |
|---|---|
| ADMINISTRADOR | Acceso completo, usuarios, auditoría, operación y finanzas |
| OPERADOR | Agricultores, parcelas, turnos e inteligencia operativa |
| TESORERIA | Cuentas, pagos, caja e inteligencia financiera |

---

## 6. Credenciales y secretos

### Credenciales de aplicación

Los nombres de usuario normalmente utilizados en desarrollo o demo son:

```text
admin
operador
tesoreria
```

Las contraseñas no deben documentarse en Git ni incrustarse en este README. Deben definirse mediante configuración local, variables de entorno o el mecanismo de carga inicial correspondiente.

Para localizar la configuración del usuario inicial sin imprimir archivos secretos completos:

```bash
grep -R -n 'ADMIN_USERNAME\|ADMIN_PASSWORD\|INITIAL_' src/main/resources src/main/java
```

Para localizar credenciales exclusivamente demo:

```bash
grep -R -n 'admin\|operador\|tesoreria' src/main/java/pe/vichaval/gestionriego/config/seed src/main/resources/application-demo.properties
```

> Nunca publiques `.env.prod`, contraseñas de PostgreSQL, contraseñas de Grafana, tokens ni hashes.

### Credenciales PostgreSQL

Se configuran en `.env.prod`:

```dotenv
POSTGRES_DB=gestion_riego
POSTGRES_USER=gestion_prod
POSTGRES_PASSWORD=CAMBIAR_POR_UN_SECRETO
DB_URL=jdbc:postgresql://postgres:5432/gestion_riego
DB_USERNAME=gestion_prod
DB_PASSWORD=CAMBIAR_POR_EL_MISMO_SECRETO
```

`POSTGRES_PASSWORD` y `DB_PASSWORD` deben coincidir para una instalación nueva.

### Credenciales Grafana

```dotenv
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=CAMBIAR_POR_UN_SECRETO
```

---

## 7. Requisitos

### Desarrollo local

- Java 21.
- Maven 3.9 o superior.
- PostgreSQL 18, o Docker Desktop.
- Git.

Comprobar versiones:

```bash
java -version
mvn -version
docker version
docker compose version
git --version
```

---

## 8. Clonar y preparar el proyecto

```bash
git clone URL_DEL_REPOSITORIO
git switch main
cd gestion-riego
```

Instalar dependencias y compilar:

```bash
mvn clean package -DskipTests
```

Ejecutar todas las verificaciones:

```bash
mvn clean verify
```

---

## 9. Ejecución local con Maven

### Opción A: PostgreSQL local

Configura las variables requeridas por el perfil de desarrollo y ejecuta:

```bash
mvn spring-boot:run
```

Abrir:

```text
http://localhost:8080/login
```

Detener:

```text
Control + C
```

### Opción B: ejecutar el JAR

```bash
mvn clean package -DskipTests
```

```bash
java -jar target/gestion-riego-0.0.3-SNAPSHOT.jar
```

---

## 10. Entorno demo

La configuración demo utiliza un PostgreSQL separado y datos ficticios.

Iniciar:

```bash
docker compose -f compose-demo.yaml up -d --build
```

Ver estado:

```bash
docker compose -f compose-demo.yaml ps
```

Ver logs:

```bash
docker compose -f compose-demo.yaml logs -f
```

Detener conservando datos:

```bash
docker compose -f compose-demo.yaml stop
```

Iniciar contenedores detenidos:

```bash
docker compose -f compose-demo.yaml start
```

Eliminar contenedores conservando volumen:

```bash
docker compose -f compose-demo.yaml down
```

Reiniciar demo desde cero, eliminando su volumen:

```bash
docker compose -f compose-demo.yaml down -v
```

```bash
docker compose -f compose-demo.yaml up -d --build
```

### Seed demo

El seed se activa mediante el perfil `demo`. La implementación usa los componentes ubicados en:

```text
src/main/java/pe/vichaval/gestionriego/config/seed/
```

Para revisar las condiciones exactas:

```bash
grep -R -n 'Profile\|ConditionalOnProperty\|demo.seed' src/main/java/pe/vichaval/gestionriego/config/seed src/main/resources
```

Para recrear completamente los datos demo:

```bash
docker compose -f compose-demo.yaml down -v
```

```bash
docker compose -f compose-demo.yaml up -d --build
```

El seed debe ser idempotente: reiniciar el contenedor no debe duplicar datos.

---

## 11. Producción local con Docker Compose

### Crear archivo de entorno

```bash
cp .env.prod.example .env.prod
```

Editar:

```bash
nano .env.prod
```

Comprobar que Git lo ignore:

```bash
git check-ignore -v .env.prod
```

Validar configuración sin revelar secretos:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml config --quiet
```

### Construir e iniciar

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
```

### Estado

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml ps
```

### Logs de aplicación

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml logs --tail=200 app
```

### Logs continuos

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml logs -f app
```

### Reiniciar solo la aplicación

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml restart app
```

### Reconstruir solo la aplicación

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build --force-recreate app
```

### Detener sin eliminar

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml stop
```

### Volver a iniciar

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml start
```

### Eliminar contenedores conservando volúmenes

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml down
```

### Reinicio total con eliminación de datos

> Advertencia: elimina PostgreSQL, Prometheus y Grafana del proyecto local.

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml down -v
```

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
```

---

## 12. Enlaces principales

| Recurso | Dirección |
|---|---|
| Login | http://127.0.0.1:8080/login |
| Dashboard | http://127.0.0.1:8080/dashboard |
| Salud | http://127.0.0.1:8080/actuator/health |
| Métricas | http://127.0.0.1:8080/actuator/prometheus |
| Swagger UI | http://127.0.0.1:8080/swagger-ui.html |
| OpenAPI JSON | http://127.0.0.1:8080/v3/api-docs |
| Prometheus | http://127.0.0.1:9090 |
| Objetivos Prometheus | http://127.0.0.1:9090/targets |
| Grafana | http://127.0.0.1:3000 |
| pgAdmin de desarrollo, si está activo | http://127.0.0.1:5050 |

La aplicación se publica únicamente en `127.0.0.1` en el entorno local de producción. Para exposición externa se requiere proxy inverso, TLS, control de red y gestión segura de secretos.

---

## 13. Health checks

Comprobar aplicación:

```bash
curl --fail --silent http://127.0.0.1:8080/actuator/health
```

Resultado esperado:

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

Comprobar PostgreSQL:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml exec postgres pg_isready -U gestion_prod -d gestion_riego
```

Comprobar Prometheus:

```bash
curl --fail --silent http://127.0.0.1:8080/actuator/prometheus
```

Buscar métricas del negocio:

```bash
curl --fail --silent http://127.0.0.1:8080/actuator/prometheus | grep '^vichayal_'
```

---

## 14. API REST v10.0

### Base URL

```text
http://127.0.0.1:8080/api/v1
```

### Agricultores

```text
GET    /api/v1/agricultores
GET    /api/v1/agricultores/{id}
POST   /api/v1/agricultores
PUT    /api/v1/agricultores/{id}
PATCH  /api/v1/agricultores/{id}/estado
DELETE /api/v1/agricultores/{id}
```

Ejemplo de creación:

```json
{
  "dni": "12345678",
  "nombres": "Agricultor",
  "apellidos": "Prueba API",
  "telefono": "999999999",
  "sector": "Norte",
  "estado": "ACTIVO"
}
```

### Parcelas

```text
GET /api/v1/parcelas
GET /api/v1/parcelas/{id}
```

### Turnos

```text
GET  /api/v1/turnos
GET  /api/v1/turnos/{id}
POST /api/v1/turnos/{id}/iniciar
POST /api/v1/turnos/{id}/completar
POST /api/v1/turnos/{id}/cancelar
```

Ejemplo de cancelación:

```json
{
  "motivo": "Suspensión temporal del servicio"
}
```

### Seguridad de la API actual

- Administrador y operador pueden acceder a agricultores, parcelas y turnos.
- Tesorería no accede a la API operativa.
- Swagger está restringido al administrador.
- v10.0 usa la sesión web existente.
- Los tokens para React, Angular y aplicación móvil se incorporarán en v10.1.

---

## 15. Migraciones Flyway

Migraciones actuales:

```text
V1  schema
V2  estados y trazabilidad
V3  conceptos y cuentas por cobrar
V4  vinculación de pagos
V5  egresos y devoluciones
V6  cierres de caja
V7  bitácora de auditoría
V8  integridad financiera y correlativos
```

Consultar historial:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml exec postgres psql -U gestion_prod -d gestion_riego -P pager=off -c "SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

No modifiques una migración ya aplicada. Crea una nueva migración para cambios posteriores.

---

## 16. Pruebas

### Pruebas unitarias

```bash
mvn test
```

### Compilar pruebas

```bash
mvn test-compile
```

### Verificación completa

```bash
mvn clean verify
```

### Ejecutar una clase unitaria

```bash
mvn -Dtest=GlobalExceptionHandlerTest test
```

### Ejecutar integraciones específicas

```bash
mvn clean verify -Dit.test=SecurityAccessIT,FlywaySchemaIT,ApiAccessIT
```

### Informes

```text
target/surefire-reports/
target/failsafe-reports/
target/site/jacoco/index.html
```

Abrir cobertura en macOS:

```bash
open target/site/jacoco/index.html
```

---

## 17. Respaldo de PostgreSQL

Crear respaldo:

```bash
ENV_FILE=.env.prod COMPOSE_FILE=compose-prod.yaml COMPOSE_PROJECT_NAME=vichayal_prod ./scripts/backup-postgres.sh
```

Listar respaldos:

```bash
ls -lh backups
```

Verificar SHA-256:

```bash
shasum -a 256 -c backups/*.dump.sha256
```

Seleccionar el respaldo más reciente:

```bash
BACKUP_FILE=$(find backups -type f -name '*.dump' -print | sort | tail -1)
echo "$BACKUP_FILE"
```

---

## 18. Restauración de prueba

```bash
RESTORE_TARGET_DB=gestion_riego_restore_test ENV_FILE=.env.prod COMPOSE_FILE=compose-prod.yaml COMPOSE_PROJECT_NAME=vichayal_prod ./scripts/restore-postgres.sh "$BACKUP_FILE"
```

Comprobar tablas:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml exec postgres psql -U gestion_prod -d gestion_riego_restore_test -P pager=off -c '\dt'
```

Comprobar Flyway restaurado:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml exec postgres psql -U gestion_prod -d gestion_riego_restore_test -P pager=off -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

Nunca ensayes una restauración directamente sobre la base productiva.

---

## 19. Mantenimiento

La auditoría conserva por defecto 365 días y ejecuta su depuración a las 02:30, zona `America/Lima`.

Variables:

```dotenv
AUDIT_RETENTION_DAYS=365
AUDIT_CLEANUP_CRON=0 30 2 * * *
```

No configures una retención menor de 30 días.

---

## 20. Prometheus y Grafana

Iniciar junto con producción:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d
```

Detener solo observabilidad:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml stop prometheus grafana
```

Iniciar nuevamente:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml start prometheus grafana
```

No uses `down -v` si deseas conservar las series de Prometheus y la configuración persistente de Grafana.

---

## 21. Integración continua

El flujo de GitHub Actions se encuentra en:

```text
.github/workflows/ci.yml
```

Acciones principales:

1. Descargar el repositorio.
2. Configurar Java 21.
3. Ejecutar `mvn clean verify`.
4. Construir la imagen Docker.
5. Comprobar que la imagen use un usuario no privilegiado.
6. Publicar informes de pruebas como artefactos.

Ejecutar localmente el equivalente principal:

```bash
mvn --batch-mode --no-transfer-progress clean verify
```

Construir la imagen:

```bash
docker build -t vichayal-riego:10.0.0 .
```

Comprobar usuario del contenedor:

```bash
docker image inspect vichayal-riego:10.0.0 --format '{{.Config.User}}'
```

Resultado esperado:

```text
10001:10001
```

---

## 22. Git y estrategia de versiones

Ver estado:

```bash
git status
```

Agregar cambios:

```bash
git add .
```

Comprobar lo preparado:

```bash
git diff --cached --name-status
```

Crear commit:

```bash
git commit -m "Incorporar API REST operativa v10 y login responsive"
```

Crear etiqueta:

```bash
git tag -a v10.0.0 -m "Vichayal Riego Web v10 API REST inicial"
```

Publicar rama:

```bash
git push -u origin feature/v10-api-rest
```

Publicar etiqueta:

```bash
git push origin v10.0.0
```

`target/`, `.env.prod` y los archivos de respaldo deben estar ignorados por Git.

---

## 23. Solución de problemas

### La aplicación no inicia por contraseña PostgreSQL

Síntoma:

```text
password authentication failed for user gestion_prod
```

Causa habitual: `.env.prod` cambió, pero el volumen conserva la contraseña anterior.

Si los datos son prescindibles:

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml down -v
```

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
```

### Puerto 8080 ocupado

```bash
lsof -i :8080
```

### Docker no disponible

```bash
docker info
```

### Ver causa de reinicio

```bash
docker inspect vichayal_prod-app-1 --format 'estado={{.State.Status}} reinicios={{.RestartCount}} salida={{.State.ExitCode}}'
```

### Ver logs recientes

```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml logs --tail=200 app
```

### Endpoint inexistente devuelve 500

Debe existir un manejador específico para `NoResourceFoundException` que responda 404. Las pruebas `SecurityAccessIT` verifican esta conducta.

### El login no cambia visualmente

La plantilla activa es:

```text
src/main/resources/templates/auth/login.html
```

El CSS exclusivo es:

```text
src/main/resources/static/css/login.css
```

El controlador original es:

```text
src/main/java/pe/vichaval/gestionriego/security/controller/LoginController.java
```

No crees un segundo `LoginController`, porque produciría un conflicto de beans.

---

## 24. Estructura relevante

```text
src/main/java/pe/vichaval/gestionriego/
├── agricultor/
├── api/v1/
│   ├── agricultor/
│   ├── parcela/
│   ├── turno/
│   ├── common/
│   ├── config/
│   └── error/
├── auditoria/
├── caja/
├── config/
├── dashboard/
├── error/
├── finanzas/
├── inteligencia/
├── mantenimiento/
├── observabilidad/
├── parcela/
├── reporte/
├── riego/
├── security/
├── tesoreria/
└── usuario/

src/main/resources/
├── db/migration/
├── static/css/
│   ├── app.css
│   └── login.css
├── templates/
│   └── auth/login.html
├── application.properties
├── application-demo.properties
└── application-prod.properties
```

---

## 25. Hoja de ruta

```text
v10.0  API REST inicial para agricultores, parcelas y turnos
v10.1  Seguridad API y cuenta del agricultor
v10.2  Centro de notificaciones persistentes
v10.3  Notificaciones push mediante Firebase Cloud Messaging
v10.4  WebSocket opcional
v11.0  Aplicación móvil del agricultor
v12.0  Inteligencia predictiva
```

---

## 26. Seguridad operativa

- No subir `.env.prod`.
- No registrar contraseñas en logs.
- No publicar Actuator, Prometheus, Grafana o PostgreSQL directamente en Internet.
- Usar TLS y proxy inverso para exposición externa.
- Rotar cualquier secreto compartido accidentalmente.
- Ejecutar la imagen con usuario `10001:10001`.
- Conservar `no-new-privileges:true`.
- Mantener PostgreSQL sin puerto público en producción.
- Realizar respaldos y pruebas de restauración.

---

## 27. Licencia y uso

Proyecto académico y administrativo orientado a la gestión de riego de Vichayal. Antes de un despliegue institucional definitivo deben revisarse licenciamiento, protección de datos personales, continuidad operativa, respaldo, recuperación y responsabilidades de administración.


## Cliente móvil
Backend congelado para React Native Android e iOS desde v10.5.


## v10.7
Paginación y filtros de listados administrativos y financieros.
