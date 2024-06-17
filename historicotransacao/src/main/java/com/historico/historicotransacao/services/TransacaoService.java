package com.historico.historicotransacao.services;

import java.util.List;
import java.util.UUID;

import com.historico.historicotransacao.dtos.historico.req.HistoricoTransacaoRequest;
import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;

public interface TransacaoService {
     void processarMensagemSQS(String mensagemSQS);
    HistoricoTransacaoResponse salvarTransacao(HistoricoTransacaoRequest request);
    HistoricoTransacaoResponse buscarTransacaoPorId(UUID id);
    List<HistoricoTransacaoResponse> transacoesUsuario(UUID idUsuario);  
}
