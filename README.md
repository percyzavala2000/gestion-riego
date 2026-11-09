# Vichayal Riego Web v10.5

## Guía de instalación y prueba en Windows

Esta guía está destinada a personas evaluadoras que recibirán el proyecto en un archivo ZIP. No es necesario tener contenedores creados previamente. **Docker Compose descargará las imágenes necesarias y creará automáticamente los contenedores, redes y volúmenes** cuando se ejecuten los comandos indicados.

La evaluación contempla dos modalidades:

1. **Demo local con seed:** recomendada para revisar rápidamente las funciones con datos ficticios.
2. **Producción local con Docker:** recomendada para comprobar el despliegue contenedorizado, observabilidad y una base inicialmente vacía.

> Versión documentada: `10.5.0`  
> Sistema operativo de esta guía: Windows 10 u 11 de 64 bits  
> Backend: Java 21 y Spring Boot 4.1.1  
> Base de datos: PostgreSQL 18

---

# 1. Herramientas necesarias

## 1.1 Instalación obligatoria

Instalar solamente estas herramientas:

### Git para Windows

Se utiliza para comandos auxiliares y control de versiones. Si solamente se recibe el ZIP, Git no es imprescindible para ejecutar el sistema, pero se recomienda para revisar cambios.

Descarga oficial:

```text
https://git-scm.com/download/win
```

Durante la instalación se pueden conservar las opciones predeterminadas.

### Docker Desktop para Windows

Se utiliza para crear PostgreSQL, la aplicación productiva, Prometheus y Grafana.

Descarga oficial:

```text
https://www.docker.com/products/docker-desktop/
```

Requisitos habituales:

- Windows 10 u 11 de 64 bits.
- Virtualización habilitada en BIOS o UEFI.
- WSL 2 habilitado.
- Al menos 4 GB de RAM disponibles para Docker Desktop. Se recomiendan 8 GB en el equipo.

Después de instalar, abrir Docker Desktop y esperar hasta que indique que el motor está ejecutándose.

### JDK 21

Es necesario para ejecutar el modo demo local con Maven.

Descarga recomendada:

```text
https://adoptium.net/temurin/releases/?version=21
```

Seleccionar:

```text
Operating System: Windows
Architecture: x64
Package Type: JDK
Version: 21 LTS
```

Durante la instalación, habilitar las opciones para configurar `JAVA_HOME` y agregar Java al `PATH`.

### Apache Maven 3.9 o superior

Se utiliza para compilar y ejecutar la aplicación demo.

Descarga oficial:

```text
https://maven.apache.org/download.cgi
```

Descomprimir Maven, por ejemplo, en:

```text
C:\Tools\apache-maven-3.9.11
```

Agregar al `PATH`:

```text
C:\Tools\apache-maven-3.9.11\bin
```

## 1.2 Navegador

Usar una versión reciente de:

- Google Chrome.
- Microsoft Edge.
- Mozilla Firefox.

## 1.3 Verificación de herramientas

Abrir **PowerShell** y ejecutar:

```powershell
java -version
mvn -version
docker version
docker compose version
```

Resultados esperados:

- Java debe indicar versión 21.
- Maven debe indicar versión 3.9 o superior y Java 21.
- Docker debe mostrar información del cliente y servidor.
- Docker Compose debe indicar versión 2.

Si `docker version` no muestra información del servidor, abrir Docker Desktop y esperar hasta que el motor termine de iniciar.

---

# 2. Preparar el proyecto recibido por correo

1. Descargar el ZIP adjunto.
2. Hacer clic derecho y seleccionar **Extraer todo**.
3. Extraer en una ruta corta, por ejemplo:

```text
C:\Proyectos\gestion-riego
```

4. Abrir PowerShell.
5. Entrar al proyecto:

```powershell
cd C:\Proyectos\gestion-riego
```

6. Comprobar los archivos principales:

```powershell
Get-ChildItem
```

Deben existir, entre otros:

```text
pom.xml
Dockerfile
compose-prod.yaml
compose-observability.yaml
compose-demo.yaml
src
scripts
```

---

# 3. Primera prueba recomendada: demo local con seed

## 3.1 Qué es el seed demo

El seed es un proceso que crea automáticamente datos ficticios para poder evaluar el sistema sin registrar todo manualmente. Puede incluir:

- Usuarios de prueba.
- Agricultores ficticios.
- Parcelas ficticias.
- Turnos en distintos estados.
- Cuentas, pagos y movimientos de prueba.
- Casos preparados para mostrar alertas operativas y financieras.

El seed se ejecuta solamente con el perfil:

```text
demo
```

No debe habilitarse en producción porque introduciría datos ficticios en una base destinada a información real.

## 3.2 Qué crea Docker automáticamente

Al ejecutar `compose-demo.yaml`, Docker:

1. Descarga `postgres:18` si no está disponible.
2. Crea el contenedor PostgreSQL demo.
3. Crea la red y el volumen necesarios.
4. Publica PostgreSQL demo en `localhost:5433`.

No es necesario crear manualmente un contenedor desde Docker Desktop.

## 3.3 Levantar PostgreSQL demo

Desde PowerShell, dentro del proyecto:

```powershell
docker compose -f compose-demo.yaml up -d
```

Comprobar:

```powershell
docker compose -f compose-demo.yaml ps
```

El contenedor PostgreSQL debe mostrarse como `healthy`.

La primera ejecución puede tardar mientras Docker descarga la imagen.

## 3.4 Ejecutar la aplicación con perfil demo

En la misma ventana de PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="demo"
mvn spring-boot:run
```

Esperar hasta observar:

```text
Started GestionRiegoApplication
```

En el primer inicio:

1. Flyway crea el esquema de PostgreSQL.
2. Se aplican las migraciones disponibles.
3. El perfil demo activa el seed.
4. Se insertan los datos ficticios.

## 3.5 Abrir el demo

Abrir en el navegador:

```text
http://localhost:8080/login
```

## 3.6 Usuarios demo

Usuarios previstos:

```text
admin
operador
tesoreria
```

Las claves de evaluación deben entregarse junto al ZIP mediante un archivo separado, por ejemplo `CREDENCIALES-EVALUACION.txt`, o por un canal privado.

No se recomienda incluir contraseñas reales en un repositorio público.

Formato sugerido para el archivo privado:

```text
ministrador
Usuario: admin
Clave: Admin123*

Operador
Usuario: operador
Clave: Operador123*

Tesorería
Usuario: tesoreria
Clave: Tesoreria123*

```

## 3.7 Detener la aplicación demo

En la terminal donde se ejecuta Maven, presionar:

```text
Control + C
```

La aplicación se detiene, pero PostgreSQL demo y sus datos permanecen disponibles.

## 3.8 Detener PostgreSQL demo conservando datos

```powershell
docker compose -f compose-demo.yaml stop
```

Volver a iniciarlo:

```powershell
docker compose -f compose-demo.yaml start
```

## 3.9 Eliminar contenedores demo conservando datos

```powershell
docker compose -f compose-demo.yaml down
```

Volver a crearlos usando el volumen existente:

```powershell
docker compose -f compose-demo.yaml up -d
```

## 3.10 Resetear el demo y ejecutar otra vez el seed

Primero detener Maven con `Control + C`.

Eliminar contenedor y volumen demo:

```powershell
docker compose -f compose-demo.yaml down -v
```

Crear una base demo nueva:

```powershell
docker compose -f compose-demo.yaml up -d
```

Ejecutar nuevamente la aplicación:

```powershell
$env:SPRING_PROFILES_ACTIVE="demo"
mvn spring-boot:run
```

El seed volverá a ejecutarse porque la base está vacía.

> `down -v` elimina los datos del entorno indicado. Utilizarlo solamente en el demo cuando se desee comenzar desde cero.

---

# 4. Segunda prueba: producción local con Docker

## 4.1 Qué significa producción local

Es la configuración de producción ejecutada en la computadora del evaluador. Docker crea:

- Aplicación Spring Boot.
- PostgreSQL 18.
- Prometheus.
- Grafana.
- Red privada.
- Volúmenes persistentes.

Direcciones:

```text
Aplicación: http://127.0.0.1:8080
Prometheus: http://127.0.0.1:9090
Grafana:    http://127.0.0.1:3000
```

La producción local empieza con una base vacía. Esto es correcto: Flyway crea las tablas, pero el seed demo no se ejecuta.

## 4.2 Detener el demo antes de producción

Si Maven sigue activo, presionar `Control + C`.

Detener PostgreSQL demo si no se utilizará:

```powershell
docker compose -f compose-demo.yaml stop
```

## 4.3 Crear `.env.prod`

```powershell
Copy-Item .env.prod.example .env.prod
```

Abrirlo en Bloc de notas:

```powershell
notepad .env.prod
```

Verificar o completar:

```dotenv
APP_VERSION=10.0.0
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
POSTGRES_DB=gestion_riego
POSTGRES_USER=gestion_prod
POSTGRES_PASSWORD=CAMBIAR_POR_UN_SECRETO
DB_URL=jdbc:postgresql://postgres:5432/gestion_riego
DB_USERNAME=gestion_prod
DB_PASSWORD=CAMBIAR_POR_EL_MISMO_SECRETO
COOKIE_SECURE=false
JAVA_OPTS=-XX:MaxRAMPercentage=75.0
TZ=America/Lima
AUDIT_RETENTION_DAYS=365
AUDIT_CLEANUP_CRON=0 30 2 * * *
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=CAMBIAR_POR_UN_SECRETO
PROMETHEUS_PORT=9090
GRAFANA_PORT=3000
```

`POSTGRES_PASSWORD` y `DB_PASSWORD` deben ser iguales en la primera instalación.

El archivo `.env.prod` contiene secretos y no debe publicarse ni enviarse a repositorios.

## 4.4 Validar configuración

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml config --quiet
```

Si el comando no imprime errores, la configuración es válida.

## 4.5 Levantar producción local

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
```

Docker realizará automáticamente:

1. Descarga de imágenes ausentes.
2. Construcción de `vichayal-riego:10.0.0`.
3. Creación de PostgreSQL.
4. Creación de redes y volúmenes.
5. Inicio de Prometheus y Grafana.
6. Ejecución de Flyway al iniciar la aplicación.

Esperar:

```powershell
Start-Sleep -Seconds 25
```

Comprobar:

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml ps
```

Resultado esperado:

```text
app          healthy
postgres     healthy
prometheus   running
grafana      running
```

## 4.6 Abrir producción local

Login:

```text
http://127.0.0.1:8080/login
```

Health check:

```text
http://127.0.0.1:8080/actuator/health
```

Swagger:

```text
http://127.0.0.1:8080/swagger-ui.html
```

Prometheus:

```text
http://127.0.0.1:9090
```

Grafana:

```text
http://127.0.0.1:3000
```

## 4.7 Base vacía en producción

La base productiva empieza vacía. Los datos reales se registran desde la aplicación después de iniciar sesión:

1. Usuarios.
2. Agricultores.
3. Parcelas.
4. Turnos.
5. Conceptos de cobro.
6. Cuentas y pagos.

No ejecutar el perfil `demo` sobre producción.

## 4.8 Ver logs

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml logs --tail=200 app
```

Logs continuos:

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml logs -f app
```

Para salir de los logs continuos, presionar `Control + C`. Los contenedores continúan encendidos.

## 4.9 Apagar producción conservando datos

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml stop
```

Volver a iniciar:

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml start
```

## 4.10 Eliminar contenedores conservando datos

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml down
```

Volver a crearlos:

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d
```

## 4.11 Reset total de producción local

> Advertencia: elimina la base de datos productiva local, las métricas de Prometheus y los datos persistentes de Grafana.

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml down -v
```

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
```

No ejecutar esta operación cuando existan datos importantes sin respaldo.

---

# 5. Diferencia entre demo y producción local

| Característica | Demo local | Producción local Docker |
|---|---|---|
| Perfil | `demo` | `prod` |
| Aplicación | Maven en Windows | Contenedor Docker |
| Dirección | `localhost:8080` | `127.0.0.1:8080` |
| PostgreSQL | Contenedor, puerto `5433` | Contenedor interno |
| Base | `gestion_riego_demo` | `gestion_riego` |
| Seed | Sí | No |
| Datos iniciales | Ficticios | Vacíos |
| Prometheus y Grafana | No requeridos | Sí |
| Finalidad | Demostración funcional | Validación del despliegue |

Aunque `localhost` y `127.0.0.1` representan el mismo equipo, esta guía los utiliza para distinguir conceptualmente ambos modos. No pueden utilizar simultáneamente el mismo puerto 8080.

---

# 6. Prueba rápida de salud

En producción local:

```powershell
Invoke-RestMethod http://127.0.0.1:8080/actuator/health
```

Debe mostrar estado:

```text
UP
```

Comprobar contenedores:

```powershell
docker ps
```

---

# 7. API REST disponible

Después de iniciar sesión como administrador u operador:

```text
GET    /api/v1/agricultores
GET    /api/v1/agricultores/{id}
POST   /api/v1/agricultores
PUT    /api/v1/agricultores/{id}
PATCH  /api/v1/agricultores/{id}/estado
DELETE /api/v1/agricultores/{id}

GET    /api/v1/parcelas
GET    /api/v1/parcelas/{id}

GET    /api/v1/turnos
GET    /api/v1/turnos/{id}
POST   /api/v1/turnos/{id}/iniciar
POST   /api/v1/turnos/{id}/completar
POST   /api/v1/turnos/{id}/cancelar
```

Swagger:

```text
http://127.0.0.1:8080/swagger-ui.html
```

---

# 8. Pruebas automatizadas opcionales

Con Docker Desktop encendido:

```powershell
mvn clean verify
```

Resultado requerido:

```text
BUILD SUCCESS
```

Las pruebas de integración crean automáticamente un PostgreSQL temporal mediante Testcontainers. No es necesario crear ese contenedor manualmente.

---

# 9. Problemas frecuentes

## Docker no responde

Abrir Docker Desktop y esperar. Después ejecutar:

```powershell
docker info
```

## Puerto 8080 ocupado

```powershell
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
```

Detener el modo que ya usa el puerto antes de iniciar el otro.

## El contenedor demo ya existe

```powershell
docker ps -a --filter name=vichayal-postgres-demo
```

Reutilizarlo:

```powershell
docker compose -f compose-demo.yaml up -d
```

## La aplicación productiva reinicia

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml logs --tail=200 app
```

## La contraseña de PostgreSQL cambió después de crear el volumen

Las variables de entorno no cambian la contraseña almacenada en un volumen existente. En una evaluación sin datos importantes, resetear producción local:

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml down -v
```

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
```

---

# 10. Secuencia mínima recomendada

## Primero: demo con datos ficticios

```powershell
cd C:\Proyectos\gestion-riego
docker compose -f compose-demo.yaml up -d
$env:SPRING_PROFILES_ACTIVE="demo"
mvn spring-boot:run
```

Abrir:

```text
http://localhost:8080/login
```

Al terminar, presionar `Control + C`.

## Segundo: producción local Docker

```powershell
docker compose -f compose-demo.yaml stop
Copy-Item .env.prod.example .env.prod
notepad .env.prod
```

Después de configurar las claves:

```powershell
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
Start-Sleep -Seconds 25
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml ps
```

Abrir:

```text
http://127.0.0.1:8080/login
```

---

# 11. Credenciales de evaluación

El responsable del proyecto debe completar y entregar este bloque por separado:

```text
DEMO

Administrador
Usuario: admin
Clave: Admin123*

Operador
Usuario: operador
Clave: Operador123*

Tesorería
Usuario: tesoreria
Clave: Tesoreria123*

Grafana
Usuario: admin
Clave: 7pcv4bG68Ox10i1hFHhG+KqthbIQoyz1WVXZWC143U4w/UkVe9LefDUd8GDjksTh
```

No utilizar claves de producción real para la evaluación.


## Backend móvil React Native

El contrato `/api/v1` está estabilizado para una aplicación React Native Android e iOS. Incluye autenticación Bearer, refresh token rotatorio, perfil, parcelas, turnos, notificaciones persistentes y registro de dispositivos Firebase.

Colección Postman: `docs/08-api/postman/Vichayal-Riego-Mobile-v10.5.postman_collection.json`.

Configuración segura predeterminada:

```dotenv
FIREBASE_ENABLED=false
MOBILE_CORS_ENABLED=false
AUTH_RATE_LIMIT_MAX_REQUESTS=10
AUTH_RATE_LIMIT_WINDOW_SECONDS=60
```
