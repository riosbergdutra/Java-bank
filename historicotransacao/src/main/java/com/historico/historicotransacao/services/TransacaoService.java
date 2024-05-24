package com.historico.historicotransacao.services;

import com.historico.historicotransacao.dtos.historico.req.HistoricoTransacaoRequest;
import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;

public interface TransacaoService {
     void processarMensagemSQS(String mensagemSQS);
    HistoricoTransacaoResponse salvarTransacao(HistoricoTransacaoRequest request);
}
