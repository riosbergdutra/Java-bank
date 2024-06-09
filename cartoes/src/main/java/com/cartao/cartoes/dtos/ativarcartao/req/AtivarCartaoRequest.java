package com.cartao.cartoes.dtos.ativarcartao.req;

public record AtivarCartaoRequest(
    String cvv,
    String numeroCartao
) {
    
}
