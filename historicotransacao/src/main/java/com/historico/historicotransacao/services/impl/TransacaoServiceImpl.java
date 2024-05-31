package com.historico.historicotransacao.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.historico.historicotransacao.Enum.TipoTransacao;
import com.historico.historicotransacao.dtos.historico.req.HistoricoTransacaoRequest;
import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;
import com.historico.historicotransacao.model.Historico;
import com.historico.historicotransacao.repository.HistoricoRepository;
import com.historico.historicotransacao.services.TransacaoService;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    @Override
    public void processarMensagemSQS(String mensagemSQS) {
        try {
            String[] partes = mensagemSQS.split("\n");

            if (partes.length >= 7 && partes[0].startsWith("ID Bancaria Origem")
                    && partes[1].startsWith("ID Usuario Origem")
                    && partes[2].startsWith("ID Bancaria Destino")
                    && partes[3].startsWith("ID Usuario Destino")
                    && partes[4].startsWith("Tipo da Transacao")
                    && partes[5].startsWith("Valor")
                    && partes[6].startsWith("data Transacao")) {

                UUID idBancariaOrigem = UUID.fromString(partes[0].split(": ")[1].trim());
                UUID idUsuarioOrigem = UUID.fromString(partes[1].split(": ")[1].trim());
                UUID idBancariaDestino = UUID.fromString(partes[2].split(": ")[1].trim());
                UUID idUsuarioDestino = UUID.fromString(partes[3].split(": ")[1].trim());
                String tipoTransacaoStr = partes[4].split(": ")[1].trim();

                TipoTransacao tipoTransacao = null;
                try {
                    tipoTransacao = TipoTransacao.valueOf(tipoTransacaoStr);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Tipo de transação inválido: " + tipoTransacaoStr);
                    return;
                }

                BigDecimal valor = new BigDecimal(partes[5].split(": ")[1].trim());
                LocalDate dataTransacao = LocalDate.parse(partes[6].split(": ")[1].trim());

                HistoricoTransacaoRequest historicoDto = new HistoricoTransacaoRequest(
                        null, idBancariaOrigem, idUsuarioOrigem,
                        idBancariaDestino, idUsuarioDestino,
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
                historicoTransacaoRequest.idBancariaDestino(),
                historicoTransacaoRequest.idUsuarioDestino(),
                historicoTransacaoRequest.tipoTransacao(),
                historicoTransacaoRequest.dataTransacao(),
                historicoTransacaoRequest.valor());

        Historico historicoSalvo = historicoRepository.save(historico);

        return new HistoricoTransacaoResponse(
                historicoSalvo.getTipoTransacao(),
                historicoSalvo.getValor(),
                historicoSalvo.getDataTransacao());
    }

    @Override
    public HistoricoTransacaoResponse buscarTransacaoPorId(UUID id) {
        Optional<Historico> historicoOpt = historicoRepository.findById(id);
        if (historicoOpt.isPresent()) {
            Historico historico = historicoOpt.get();
            return new HistoricoTransacaoResponse(
                    historico.getTipoTransacao(),
                    historico.getValor(),
                    historico.getDataTransacao());
        } else {
            throw new RuntimeException("Transação não encontrada com o ID: " + id);
        }
    }
}
