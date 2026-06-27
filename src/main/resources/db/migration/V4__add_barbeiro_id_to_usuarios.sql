ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS barbeiro_id UUID;

ALTER TABLE usuarios ADD CONSTRAINT fk_usuarios_barbeiro FOREIGN KEY (barbeiro_id) REFERENCES barbeiros(id) ON DELETE SET NULL;
