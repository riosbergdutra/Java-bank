package com.bancaria.contabancaria.services.impl;

import org.springframework.stereotype.Component;
import io.awspring.cloud.sqs.annotation.SqsListener;

import com.bancaria.contabancaria.services.BancariaService;
//ouvidor da mensagem sqs
@Component
public class BancariaSqsListener {
    private final BancariaService bancariaService;

    public BancariaSqsListener(BancariaService bancariaService) {
        this.bancariaService = bancariaService;
    }

    @SqsListener("usuarios")
    public void receberMensagemSQS(String mensagemSQS) {
        try {
            // Aqui você pode processar a mensagem SQS como uma String simples
            System.out.println("Mensagem SQS recebida: " + mensagemSQS);

            // Você pode chamar o serviço de conta bancária diretamente ou converter a String para o formato desejado, se necessário
            bancariaService.processarMensagemSQS(mensagemSQS);
        } catch (Exception e) {
            // Tratamento de exceção adequado, se necessário
            e.printStackTrace();
        }
    }
}
