package com.bancaria.contabancaria.dtos.transferencia.request;

import java.math.BigDecimal;
//requisição da transferencia
public record TransferenciaRequestDto(
    String ChaveOrigem,
    String ChaveDestino,
    BigDecimal valor
) {
}
