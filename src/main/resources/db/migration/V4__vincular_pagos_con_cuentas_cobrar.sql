ALTER TABLE pagos ADD COLUMN cuenta_cobrar_id BIGINT REFERENCES cuentas_cobrar(id);
CREATE INDEX idx_pagos_cuenta ON pagos(cuenta_cobrar_id);
