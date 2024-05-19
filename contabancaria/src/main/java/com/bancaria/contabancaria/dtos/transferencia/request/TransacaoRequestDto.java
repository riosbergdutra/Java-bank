package com.bancaria.contabancaria.dtos.transferencia.request;

import java.math.BigDecimal;
//requisição da transacão
public record TransacaoRequestDto(
    String ChaveOrigem,
    String ChaveDestino,
    BigDecimal valor
) {
}
