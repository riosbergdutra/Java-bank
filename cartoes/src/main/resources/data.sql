CREATE TABLE IF NOT EXISTS cartoes (
    id_cartao UUID PRIMARY KEY,
    id_usuario UUID UNIQUE REFERENCES usuarios(id_usuario),
    id_bancaria UUID UNIQUE bancaria(id_bancaria),
    cvv VARCHAR(255) UNIQUE,
    numero_cartao VARCHAR(255) UNIQUE,
    status_cartao VARCHAR(50),
    senha VARCHAR(255) UNIQUE,
    debito VARCHAR(50),
    credito VARCHAR(50),
    saldo_bancaria NUMERIC NOT NULL
);

