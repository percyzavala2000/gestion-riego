# Firebase Push v10.3 para React Native

La integración admite una sola aplicación React Native para Android e iOS. PostgreSQL conserva la notificación; Firebase Cloud Messaging es únicamente el canal push.

## Archivos del cliente móvil
- Android: `android/app/google-services.json`.
- iOS: `ios/VichayalRiego/GoogleService-Info.plist` y configuración APNs.

## Credencial del backend
El servidor usa `GOOGLE_APPLICATION_CREDENTIALS`. El JSON de cuenta de servicio se mantiene fuera del repositorio y se monta como secreto de solo lectura.

## Arranque seguro
`FIREBASE_ENABLED=false` es el valor predeterminado. Así, demo, pruebas automatizadas y Docker pueden iniciar sin credenciales reales.
