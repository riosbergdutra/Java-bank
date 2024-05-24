package com.historico.historicotransacao.services;

import org.springframework.stereotype.Component;
import io.awspring.cloud.sqs.annotation.SqsListener;

@Component
public class HistoricoSqsListener {
    private final TransacaoService transacaoService;

    public HistoricoSqsListener(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @SqsListener("transacao")
    public void receberMensagemSQS(String mensagemSQS) {
        try {
            System.out.println("Mensagem SQS recebida: " + mensagemSQS);
            transacaoService.processarMensagemSQS(mensagemSQS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
