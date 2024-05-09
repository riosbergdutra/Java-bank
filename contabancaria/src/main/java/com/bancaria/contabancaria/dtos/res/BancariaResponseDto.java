package com.bancaria.contabancaria.dtos.res;

import java.math.BigDecimal;
import com.bancaria.contabancaria.enums.TipoConta;

public record BancariaResponseDto(
    TipoConta tipoConta,
    BigDecimal saldo

) {
    
}
