package com.historico.historicotransacao.services.impl;

import com.historico.historicotransacao.Enum.TipoTransacao;
import com.historico.historicotransacao.dtos.historico.req.HistoricoTransacaoRequest;
import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;
import com.historico.historicotransacao.model.Historico;
import com.historico.historicotransacao.repository.HistoricoRepository;
import com.historico.historicotransacao.services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    @Override
    public void processarMensagemSQS(String mensagemSQS) {
        try {
            String[] partes = mensagemSQS.split("\n");
    
            if (partes.length >= 9 && partes[0].startsWith("Chave Origem") && partes[1].startsWith("ID Bancaria Origem")
                    && partes[2].startsWith("Chave Destino") && partes[3].startsWith("ID Usuario Origem")
                    && partes[4].startsWith("ID Bancaria Destino") && partes[5].startsWith("ID Usuario Destino")
                    && partes[6].startsWith("Tipo da Transacao") && partes[7].startsWith("Valor")
                    && partes[8].startsWith("data Transacao")) {
    
                String chaveOrigem = partes[0].split(": ")[1].trim();
                UUID idBancariaOrigem = UUID.fromString(partes[1].split(": ")[1].trim());
                String chaveDestino = partes[2].split(": ")[1].trim();
                UUID idUsuarioOrigem = UUID.fromString(partes[3].split(": ")[1].trim());
                UUID idBancariaDestino = UUID.fromString(partes[4].split(": ")[1].trim());
                UUID idUsuarioDestino = UUID.fromString(partes[5].split(": ")[1].trim());
                String tipoTransacaoStr = partes[6].split(": ")[1].trim();
                
                TipoTransacao tipoTransacao = null;
                try {
                    tipoTransacao = TipoTransacao.valueOf(tipoTransacaoStr);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Tipo de transação inválido: " + tipoTransacaoStr);
                    return; 
                }
                
                BigDecimal valor = new BigDecimal(partes[7].split(": ")[1].trim());
                LocalDate dataTransacao = LocalDate.parse(partes[8].split(": ")[1].trim());
    
                HistoricoTransacaoRequest historicoDto = new HistoricoTransacaoRequest(
                        null, idBancariaOrigem, idUsuarioOrigem, chaveOrigem,
                        idBancariaDestino, idUsuarioDestino, chaveDestino,
                        tipoTransacao, dataTransacao, valor);
    
                salvarTransacao(historicoDto);
                System.out.println("Transação processada e registrada com sucesso.");
            } else {
                System.out.println("Mensagem incompleta ou inválida.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public HistoricoTransacaoResponse salvarTransacao(HistoricoTransacaoRequest historicoTransacaoRequest) {
        Historico historico = new Historico(
                UUID.randomUUID(),
                historicoTransacaoRequest.idBancariaOrigem(),
                historicoTransacaoRequest.idUsuarioOrigem(),
                historicoTransacaoRequest.chaveOrigem(),
                historicoTransacaoRequest.idBancariaDestino(),
                historicoTransacaoRequest.idUsuarioDestino(),
                historicoTransacaoRequest.chaveDestino(),
                historicoTransacaoRequest.tipoTransacao(),
                historicoTransacaoRequest.dataTransacao(),
                historicoTransacaoRequest.valor());

        Historico historicoSalvo = historicoRepository.save(historico);

        return new HistoricoTransacaoResponse(
                historicoSalvo.getChaveOrigem(),
                historicoSalvo.getChaveDestino(),
                historicoSalvo.getTipoTransacao(),
                historicoSalvo.getValor(),
                historicoSalvo.getDataTransacao());
    }
}
