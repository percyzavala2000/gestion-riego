# Operación de respaldos v9.4

`scripts/backup-postgres.sh` produce un dump en formato personalizado, valida su catálogo y genera SHA-256. `scripts/rotate-backups.sh` elimina respaldos que superan la retención. `scripts/restore-postgres.sh` restaura únicamente en una base temporal y rechaza nombres de destino peligrosos.

Prueba al menos mensualmente: respaldo, verificación, restauración temporal, conteo de tablas, historial Flyway y acceso de lectura. La existencia de un archivo no garantiza recuperabilidad.
