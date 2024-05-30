CREATE TABLE IF NOT EXISTS historico (
    id_transacao UUID PRIMARY KEY,
    id_usuarioorigem UUID NOT NULL REFERENCES usuarios(id_usuario),
    id_bancariaorigem UUID NOT NULL REFERENCES bancaria(id_bancaria),
    chaveorigem VARCHAR(255) NOT NULL,

    id_bancariadestino UUID NOT NULL REFERENCES bancaria(id_bancaria),
    id_usuariodestino UUID NOT NULL  REFERENCES usuarios(id_usuario),
    chavedestino VARCHAR(255) NOT NULL
    valor NUMERIC NOT NULL,
    tipo_transacao VARCHAR(255) NOT NULL,
    data_transacao DATE NOT NULL
);