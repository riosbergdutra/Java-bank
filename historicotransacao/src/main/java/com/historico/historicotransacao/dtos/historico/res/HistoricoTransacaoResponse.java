package com.historico.historicotransacao.dtos.historico.res;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.historico.historicotransacao.Enum.TipoTransacao;


public record HistoricoTransacaoResponse(
        TipoTransacao tipoTransacao,
        BigDecimal valor,
        LocalDate dataTransacao

) {}
