CREATE TABLE IF NOT EXISTS usuarios (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome TEXT NOT NULL,
  matricula TEXT NOT NULL UNIQUE,
  setor TEXT NOT NULL,
  cargo TEXT NOT NULL,
  uid_rfid TEXT UNIQUE,
  senha_hash TEXT,
  ativo INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS documentos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome_documento TEXT NOT NULL,
  codigo_documento TEXT NOT NULL UNIQUE,
  setor TEXT NOT NULL,
  revisao TEXT NOT NULL,
  caminho_pdf TEXT NOT NULL,
  hash_arquivo TEXT NOT NULL,
  status TEXT NOT NULL,
  aprovado_por TEXT,
  data_publicacao TEXT
);

CREATE TABLE IF NOT EXISTS historico_acessos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id INTEGER NOT NULL,
  documento_id INTEGER NOT NULL,
  data_hora TEXT NOT NULL,
  acao TEXT NOT NULL,
  ip_dispositivo TEXT,
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
  FOREIGN KEY (documento_id) REFERENCES documentos (id)
);

CREATE TABLE IF NOT EXISTS log_auditoria_alteracoes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  documento_id INTEGER NOT NULL,
  usuario_id INTEGER NOT NULL,
  revisao_anterior TEXT NOT NULL,
  revisao_nova TEXT NOT NULL,
  data_hora TEXT NOT NULL,
  motivo TEXT,
  FOREIGN KEY (documento_id) REFERENCES documentos (id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

