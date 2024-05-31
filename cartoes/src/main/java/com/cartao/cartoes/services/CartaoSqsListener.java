package com.cartao.cartoes.services;

import org.springframework.stereotype.Component;
import io.awspring.cloud.sqs.annotation.SqsListener;

//ouvidor da mensagem sqs
@Component
public class CartaoSqsListener {
    private final CartaoService cartaoService;

    public CartaoSqsListener(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @SqsListener("cartao")
    public void receberMensagemSQS(String mensagemSQS) {
        try {
            // Aqui você pode processar a mensagem SQS como uma String simples
            System.out.println("Mensagem SQS recebida: " + mensagemSQS);

            // Você pode chamar o serviço de conta bancária diretamente ou converter a String para o formato desejado, se necessário
            cartaoService.processarMensagemSQSCriarCartao(mensagemSQS);
        } catch (Exception e) {
            // Tratamento de exceção adequado, se necessário
            e.printStackTrace();
        }
    }
}
