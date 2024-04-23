package com.conta.usuarios.dtos.res;
//res de response

import java.sql.Date;

import com.conta.usuarios.enums.TipoConta;

public record UsuarioResponseDto(
    String nome,
    String email,
    String cpf,
    Date dataNascimento,
    String endereco,
    TipoConta tipoConta,
    Date dataConta
) {

}
