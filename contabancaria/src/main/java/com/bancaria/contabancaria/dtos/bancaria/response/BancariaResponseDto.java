package com.bancaria.contabancaria.dtos.bancaria.response;

import java.math.BigDecimal;
import com.bancaria.contabancaria.enums.TipoConta;
//resposta da bancaria
public record BancariaResponseDto(
        TipoConta tipoConta,
        BigDecimal saldo

) {

}
