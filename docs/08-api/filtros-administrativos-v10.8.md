# Filtros administrativos v10.8

## Reportes operativos

- Agricultores: `aPage`, `aSize`.
- Parcelas: `pPage`, `pSize`.
- Turnos: `tPage`, `tSize`.
- Filtros comunes: `q`, `estado`, `sector`, `cultivo`, `agricultorId`, `parcelaId`, `desde`, `hasta`.

## Reportes financieros

- Pagos: `pgPage`, `pgSize`, `estadoPago`, `metodo`.
- Cuentas: `ccPage`, `ccSize`, `estadoCuenta`, `vencimiento`.
- Filtros comunes: `q`, `agricultorId`, `desde`, `hasta`.
- HTML se pagina; PDF y Excel exportan todo el conjunto filtrado.

## Auditoría

Parámetros: `q`, `usuario`, `metodo`, `ruta`, `correlacionId`, `familiaHttp`, `fechaDesde`, `fechaHasta`, `page`, `size`.

Familias HTTP: `2XX`, `3XX`, `4XX`, `5XX`.

## Usuarios internos

Parámetros: `q`, `rol`, `estado`, `page`, `size`.

Roles: `ADMINISTRADOR`, `TESORERIA`, `OPERADOR`.

Estados: `ACTIVO`, `INACTIVO`.
