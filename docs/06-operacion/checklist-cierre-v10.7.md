# Checklist de cierre v10.7

1. `mvn clean verify` finaliza con `BUILD SUCCESS`.
2. Agricultores conserva filtros al cambiar de página.
3. Turnos conserva acciones de estado.
4. Notificaciones permite leer y archivar.
5. Pagos conserva confirmación, rechazo y anulación.
6. Cuentas conserva pagos parciales, pago total y anulación.
7. Parcelas conserva protección del historial.
8. Caja mantiene paginadores independientes.
9. Docker inicia saludable con imagen `10.7.0`.
10. Flyway conserva V1 a V14 sin nuevas migraciones.
11. No se incluyen respaldos, `target`, `.env.prod` ni credenciales Firebase en Git.
