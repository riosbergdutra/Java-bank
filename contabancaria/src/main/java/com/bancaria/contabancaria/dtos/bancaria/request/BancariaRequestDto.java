package com.bancaria.contabancaria.dtos.bancaria.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.bancaria.contabancaria.enums.TipoConta;
//requisição da bancaria
public record BancariaRequestDto(
     UUID idBancaria,
     UUID idUsuario,
     TipoConta tipoConta,
     BigDecimal saldo,
     LocalDate dataConta

) {

}
