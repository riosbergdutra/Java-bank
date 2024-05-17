CREATE TABLE IF NOT EXISTS bancaria (
    id_bancaria UUID PRIMARY KEY,
    id_usuario UUID REFERENCES usuarios(id_usuario)  NOT NULL,
    chave VARCHAR(255) UNIQUE,
    tipo_conta VARCHAR(255) NOT NULL,
    saldo NUMERIC NOT NULL,
    data_conta DATE NOT NULL,
);