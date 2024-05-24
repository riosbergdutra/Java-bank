package com.historico.historicotransacao.dtos.historico.req;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.historico.historicotransacao.Enum.TipoTransacao;


public record HistoricoTransacaoRequest(
        UUID idTransacao,
        UUID idBancariaOrigem,
        UUID idUsuarioOrigem,
        String chaveOrigem,
        UUID idBancariaDestino,
        UUID idUsuarioDestino,
        String chaveDestino,
        TipoTransacao tipoTransacao,
        LocalDate dataTransacao,
        BigDecimal valor
) {
}
