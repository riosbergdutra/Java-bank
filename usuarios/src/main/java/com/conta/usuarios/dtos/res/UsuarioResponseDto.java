package com.conta.usuarios.dtos.res;
//res de response

import java.time.LocalDate;

import com.conta.usuarios.enums.TipoConta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record UsuarioResponseDto(
    String nome,
    String email,
    String cpf,
    TipoConta tipoConta,
    LocalDate dataConta
) {

   

public static UsuarioResponseDto fromJson(String json) {
      if (json == null || json.isEmpty()) {
          throw new IllegalArgumentException("A string JSON fornecida está vazia ou nula");
      }
      try {
          ObjectMapper objectMapper = new ObjectMapper();
          return objectMapper.readValue(json, UsuarioResponseDto.class);
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
