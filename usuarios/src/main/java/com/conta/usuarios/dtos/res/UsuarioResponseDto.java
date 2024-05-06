package com.conta.usuarios.dtos.res;
//res de response

import java.time.LocalDate;

import com.conta.usuarios.enums.TipoConta;

public record UsuarioResponseDto(
    String nome,
    String email,
    String cpf,
    TipoConta tipoConta,
    LocalDate dataConta
) {

}
