package com.bancaria.contabancaria.dtos.transferencia.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bancaria.contabancaria.enums.TipoTransacao;
//requisição da transacão
public record TransacaoRequestDto(
    TipoTransacao tipoTransacao,
    String ChaveOrigem,
    String ChaveDestino,
    BigDecimal valor,
    LocalDate dataTransacao
) {}
