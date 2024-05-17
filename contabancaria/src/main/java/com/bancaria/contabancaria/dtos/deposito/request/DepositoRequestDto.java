package com.bancaria.contabancaria.dtos.deposito.request;

import java.math.BigDecimal;

public record DepositoRequestDto(
    String chave,
    BigDecimal valor
) {
    
}
