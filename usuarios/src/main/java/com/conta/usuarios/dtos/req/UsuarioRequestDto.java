package com.conta.usuarios.dtos.req;
//req de request



import java.sql.Date;

import com.conta.usuarios.enums.RoleEnum;
import com.conta.usuarios.enums.TipoConta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static UsuarioRequestDto fromJson(String json) {
      if (json == null || json.isEmpty()) {
          throw new IllegalArgumentException("A string JSON fornecida está vazia ou nula");
      }
      try {
          ObjectMapper objectMapper = new ObjectMapper();
          return objectMapper.readValue(json, UsuarioRequestDto.class);
      } catch (JsonProcessingException e) {
          throw new IllegalArgumentException("Erro ao converter JSON para UsuarioDto", e);
      }
  }

   public String toJson() {  
     try {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Erro ao converter UsuarioDto para JSON", e);
    }
  } 
    
}
