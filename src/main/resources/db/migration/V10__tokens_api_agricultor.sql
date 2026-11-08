ALTER TABLE sesiones_agricultor ADD COLUMN access_token_hash VARCHAR(64);
ALTER TABLE sesiones_agricultor ADD COLUMN access_expiracion TIMESTAMPTZ;
CREATE UNIQUE INDEX uk_sesion_agricultor_access_hash ON sesiones_agricultor(access_token_hash) WHERE access_token_hash IS NOT NULL;
CREATE INDEX idx_sesion_agricultor_access_estado ON sesiones_agricultor(access_token_hash, revocado, access_expiracion);
