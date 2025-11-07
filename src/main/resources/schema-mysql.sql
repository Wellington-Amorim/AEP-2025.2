-- esquema para MySQL
CREATE TABLE IF NOT EXISTS doadores (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  contato VARCHAR(120) NOT NULL UNIQUE,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS beneficiarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  necessidade VARCHAR(200) NOT NULL,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- criar doacoes sem FK primeiro para evitar erros de ordering
CREATE TABLE IF NOT EXISTS doacoes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  doador_id INT NOT NULL,
  beneficiario_id INT NULL,
  item VARCHAR(120) NOT NULL,
  quantidade INT NOT NULL,
  status VARCHAR(20) NOT NULL,
  data_doacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  data_distribuicao TIMESTAMP NULL
);

-- Drop e recriar constraints de forma segura
SET @fk1 = (SELECT IF(
    EXISTS(
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS 
        WHERE CONSTRAINT_SCHEMA = DATABASE()
        AND TABLE_NAME = 'doacoes'
        AND CONSTRAINT_NAME = 'fk_doacoes_doador'
    ),
    'SELECT 1',
    'ALTER TABLE doacoes ADD CONSTRAINT fk_doacoes_doador FOREIGN KEY (doador_id) REFERENCES doadores(id)'
));
PREPARE stmt FROM @fk1;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk2 = (SELECT IF(
    EXISTS(
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS 
        WHERE CONSTRAINT_SCHEMA = DATABASE()
        AND TABLE_NAME = 'doacoes'
        AND CONSTRAINT_NAME = 'fk_doacoes_beneficiario'
    ),
    'SELECT 1',
    'ALTER TABLE doacoes ADD CONSTRAINT fk_doacoes_beneficiario FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios(id)'
));
PREPARE stmt FROM @fk2;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop e recriar índices de forma segura
SET @idx1 = (SELECT IF(
    EXISTS(
        SELECT 1 FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'doacoes'
        AND INDEX_NAME = 'idx_doacoes_status'
    ),
    'SELECT 1',
    'CREATE INDEX idx_doacoes_status ON doacoes(status)'
));
PREPARE stmt FROM @idx1;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx2 = (SELECT IF(
    EXISTS(
        SELECT 1 FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'doacoes'
        AND INDEX_NAME = 'idx_doacoes_doador'
    ),
    'SELECT 1',
    'CREATE INDEX idx_doacoes_doador ON doacoes(doador_id)'
));
PREPARE stmt FROM @idx2;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx3 = (SELECT IF(
    EXISTS(
        SELECT 1 FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'doacoes'
        AND INDEX_NAME = 'idx_doacoes_beneficiario'
    ),
    'SELECT 1',
    'CREATE INDEX idx_doacoes_beneficiario ON doacoes(beneficiario_id)'
));
PREPARE stmt FROM @idx3;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
