package com.conta.usuarios.dtos.req;
//req de request



import java.time.LocalDate;
import java.util.UUID;

import com.conta.usuarios.enums.TipoConta;


public record UsuarioRequestDto(
   UUID id_usuario,
   String nome,
   String email,
   String senha,
   String cpf,
   TipoConta tipoConta,
   LocalDate dataConta) {
    
}
