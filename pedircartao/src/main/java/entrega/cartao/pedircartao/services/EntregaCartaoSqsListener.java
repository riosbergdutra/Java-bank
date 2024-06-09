package entrega.cartao.pedircartao.services;

import org.springframework.stereotype.Component;

import io.awspring.cloud.sqs.annotation.SqsListener;

//ouvidor da mensagem sqs
@Component
public class EntregaCartaoSqsListener {
    private final EntregaCartaoService entregaCartaoService;

    public EntregaCartaoSqsListener(EntregaCartaoService entregaCartaoService) {
        this.entregaCartaoService = entregaCartaoService;
    }

    @SqsListener("entregacartao")
    public void receberMensagemSQS(String mensagemSQS) {
        try {
            // Aqui você pode processar a mensagem SQS como uma String simples
            System.out.println("Mensagem SQS recebida: " + mensagemSQS);

            // Você pode chamar o serviço de conta bancária diretamente ou converter a String para o formato desejado, se necessário
            entregaCartaoService.processarMensagemSQSCriarEntregaCartao(mensagemSQS);
        } catch (Exception e) {
            // Tratamento de exceção adequado, se necessário
            e.printStackTrace();
        }
    }
}