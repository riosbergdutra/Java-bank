package com.bancaria.contabancaria.dtos.bancariasusuario.res;

import java.math.BigDecimal;

import com.bancaria.contabancaria.enums.TipoConta;

public record ContaBancariaResponse(
    TipoConta tipoConta,
    BigDecimal saldo
) {
    
}
