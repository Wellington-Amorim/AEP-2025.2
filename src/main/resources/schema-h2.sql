-- esquema para H2 (compatível com H2 in-memory usado em testes)
CREATE TABLE IF NOT EXISTS doadores (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  contato VARCHAR(120) NOT NULL UNIQUE,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS beneficiarios (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  necessidade VARCHAR(200) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- criar tabela de doações sem constraints primeiro (evita ordering issues)
CREATE TABLE IF NOT EXISTS doacoes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  doador_id INT NOT NULL,
  beneficiario_id INT NULL,
  item VARCHAR(120) NOT NULL,
  quantidade INT NOT NULL,
  status VARCHAR(20) NOT NULL,
  data_doacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  data_distribuicao TIMESTAMP NULL
);

-- adicionar constraints/foreign keys após criar as tabelas
ALTER TABLE doacoes ADD CONSTRAINT fk_doacoes_doador FOREIGN KEY (doador_id) REFERENCES doadores(id);
ALTER TABLE doacoes ADD CONSTRAINT fk_doacoes_beneficiario FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios(id);

CREATE INDEX IF NOT EXISTS idx_doacoes_status ON doacoes(status);
CREATE INDEX IF NOT EXISTS idx_doacoes_doador ON doacoes(doador_id);
CREATE INDEX IF NOT EXISTS idx_doacoes_beneficiario ON doacoes(beneficiario_id);
