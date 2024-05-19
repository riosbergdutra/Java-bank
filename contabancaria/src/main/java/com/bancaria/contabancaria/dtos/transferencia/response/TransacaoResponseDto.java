package com.bancaria.contabancaria.dtos.transferencia.response;
//resposta da transação
public record TransacaoResponseDto(
    boolean sucesso,
    String mensagem
) {
    
}
