package com.conta.usuarios.dtos.req;
//req de request



import java.sql.Date;

import com.conta.usuarios.enums.RoleEnum;
import com.conta.usuarios.enums.TipoConta;

public record UsuarioRequestDto(
   Long id_usuario,
   String nome,
   String email,
   String senha,
   String cpf,
   Date dataNascimento,
   String endereco,
   String cep,
   RoleEnum role,
   TipoConta tipoConta,
   Date dataConta) {
    
}
