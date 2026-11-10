# Checklist de cierre v10.8

1. Java 21 activo en Maven.
2. `mvn clean verify` termina con `BUILD SUCCESS`.
3. Reportes operativos conservan todos los filtros al navegar.
4. Reportes financieros mantienen paginadores independientes.
5. PDF incluye usuario, filtros, registros y totales.
6. Excel abre sin reparación y contiene hojas `Criterios` y `Pagos` o `Cuentas`.
7. Auditoría carga sin SQLState `42P18`.
8. Auditoría conserva filtros y usa zona `America/Lima`.
9. Usuarios preserva la protección del último administrador activo.
10. Docker inicia saludable con imagen `10.8.0`.
11. No se incluyen `.env.prod`, credenciales, respaldos, PDF, XLSX ni `target` en Git.
