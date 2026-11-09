# Checklist de cierre del backend móvil v10.5

1. `mvn clean verify` termina sin fallos.
2. Docker aplica Flyway V1 a V14.
3. Firebase inicia con `FIREBASE_ENABLED=false` sin credenciales.
4. El login devuelve `429` después del límite configurado.
5. Los logs no imprimen access token, refresh token ni token FCM.
6. CORS permanece deshabilitado para React Native nativo.
7. `/actuator/health` devuelve `UP`.
8. Prometheus publica dispositivos activos y entregas push.
9. Se prueba respaldo y restauración en una base temporal.
10. OpenAPI queda congelado antes de iniciar React Native.
