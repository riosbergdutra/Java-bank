package com.bancaria.contabancaria.dtos.req;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.bancaria.contabancaria.enums.TipoConta;

public record BancariaRequestDto(
     UUID idBancaria,
     UUID idUsuario,
     TipoConta tipoConta,
     BigDecimal saldo,
     LocalDate dataConta

) {

}
