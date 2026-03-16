ALTER TABLE barbeiros ADD COLUMN IF NOT EXISTS usuario_id UUID;
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS usuario_id UUID;
ALTER TABLE servicos ADD COLUMN IF NOT EXISTS usuario_id UUID;

ALTER TABLE barbeiros ADD CONSTRAINT fk_barbeiros_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
ALTER TABLE clientes ADD CONSTRAINT fk_clientes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
ALTER TABLE servicos ADD CONSTRAINT fk_servicos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL;
