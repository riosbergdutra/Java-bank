CREATE TABLE IF NOT EXISTS historico (
    id_transacao UUID PRIMARY KEY,
    id_usuarioorigem UUID NOT NULL,
    id_bancariaorigem UUID NOT NULL,
    chaveorigem VARCHAR(255) NOT NULL,

    id_bancariadestino UUID NOT NULL,
    id_usuariodestino UUID NOT NULL,
    chavedestino VARCHAR(255) NOT NULL
    valor_transacao NUMERIC NOT NULL,
    tipo_transacao VARCHAR(255) NOT NULL,
    data_transacao DATE NOT NULL
);