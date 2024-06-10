package entrega.cartao.pedircartao.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entrega.cartao.pedircartao.enums.EstadoBrasil;
import entrega.cartao.pedircartao.model.EntregaCartao;
import entrega.cartao.pedircartao.repository.EntregaCartaoRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class EntregaCartaoService {

    @Autowired
    private EntregaCartaoRepository entregaCartaoRepository;

    public void processarMensagemSQSCriarEntregaCartao(String mensagemSQS) {
        try {
            // Dividir a mensagem em linhas
            String[] partes = mensagemSQS.split("\n");

            // Verificar se todos os campos necessários estão presentes
            if (partes.length >= 8 &&
                    partes[0].startsWith("id do Cartao") &&
                    partes[1].startsWith("id do Usuario") && 
                    partes[2].startsWith("Rua") && 
                    partes[3].startsWith("Cidade") && 
                    partes[4].startsWith("Estado")  && 
                    partes[5].startsWith("CEP") &&
                    partes[6].startsWith("Número") && 
                    partes[7].startsWith("Complemento")) {

                // Extrair os dados da mensagem
                UUID idCartao = UUID.fromString(partes[0].split(": ")[1].trim());
                UUID idUsuario = UUID.fromString(partes[1].split(": ")[1].trim());
                String rua = partes[2].split(": ")[1].trim();
                String cidade = partes[3].split(": ")[1].trim();
                String estadoStr = partes[4].split(": ")[1].trim();
                EstadoBrasil estado = EstadoBrasil.valueOf(estadoStr);
                String cep = partes[5].split(": ")[1].trim();
                String numero = partes[6].split(": ")[1].trim();
                String complemento = partes[7].split(": ")[1].trim();

                // Criar uma nova entrega de cartão
                EntregaCartao entregaCartao = new EntregaCartao();
                entregaCartao.setIdCartao(idCartao);
                entregaCartao.setIdUsuario(idUsuario);
                entregaCartao.setRua(rua);
                entregaCartao.setCidade(cidade);
                entregaCartao.setEstado(estado);
                entregaCartao.setCep(cep);
                entregaCartao.setNumero(numero);
                entregaCartao.setComplemento(complemento);
                entregaCartao.setDataPedido(LocalDate.now());
                // Salvar a entrega de cartão no banco de dados
                entregaCartaoRepository.save(entregaCartao);
            } else {
                // Mensagem incompleta ou inválida
                System.out.println("Mensagem incompleta ou inválida.");
            }
        } catch (Exception e) {
            // Tratar exceções
            e.printStackTrace();
            System.out.println("Erro ao processar a mensagem: " + e.getMessage());
        }
    }
}
