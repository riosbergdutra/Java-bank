CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    role VARCHAR(255) NOT NULL,
    tipo_conta VARCHAR(255),
    data_conta DATE NOT NULL
);
