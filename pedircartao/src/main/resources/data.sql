CREATE TABLE IF NOT EXISTS entregacartao (
    id_entrega UUID PRIMARY KEY,
    id_cartao UUID UNIQUE,
    id_usuario UUID UNIQUE,
    rua VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    cep VARCHAR(255) NOT NULL,
    numero VARCHAR(255) NOT NULL,
    complemento VARCHAR(255) NOT NULL,
    data_pedido DATE NOT NULL
);
