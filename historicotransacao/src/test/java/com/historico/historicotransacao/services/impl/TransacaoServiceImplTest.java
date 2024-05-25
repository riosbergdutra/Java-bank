package com.historico.historicotransacao.services.impl;

import com.historico.historicotransacao.Enum.TipoTransacao;
import com.historico.historicotransacao.dtos.historico.req.HistoricoTransacaoRequest;
import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;
import com.historico.historicotransacao.model.Historico;
import com.historico.historicotransacao.repository.HistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransacaoServiceImplTest {

    @Mock
    private HistoricoRepository historicoRepository;

    @InjectMocks
    private TransacaoServiceImpl transacaoService;

    @SuppressWarnings("deprecation")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testProcessarMensagemSQS_ValidMessage() {
        // Mocking the input message
        String mensagemSQS = "Chave Origem: ABC123\n" +
                             "ID Bancaria Origem: 123e4567-e89b-12d3-a456-556642440000\n" +
                             "Chave Destino: XYZ456\n" +
                             "ID Usuario Origem: 123e4567-e89b-12d3-a456-556642440001\n" +
                             "ID Bancaria Destino: 123e4567-e89b-12d3-a456-556642440002\n" +
                             "ID Usuario Destino: 123e4567-e89b-12d3-a456-556642440003\n" +
                             "Tipo da Transacao: DEBITO\n" +
                             "Valor: 100.00\n" +
                             "data Transacao: 2024-05-25";

        // Printing the received message
        System.out.println("Mensagem recebida do SQS:");
        System.out.println(mensagemSQS);

        // Setting up the expected behavior of the repository
        when(historicoRepository.save(any())).thenReturn(new Historico(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ABC123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                "XYZ456",
                TipoTransacao.DEBITO,
                LocalDate.now(),
                BigDecimal.valueOf(100.00)
        ));

        // Calling the method under test
        transacaoService.processarMensagemSQS(mensagemSQS);

        // Verifying if the save method was called
        verify(historicoRepository, times(1)).save(any());
    }

    @Test
    void testSalvarTransacao() {
        // Creating a sample request
        HistoricoTransacaoRequest request = new HistoricoTransacaoRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ABC123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                "XYZ456",
                TipoTransacao.DEBITO,
                LocalDate.now(),
                BigDecimal.valueOf(100.00)
        );

        // Printing the received request
        System.out.println("Request recebida:");
        System.out.println(request);

        // Mocking the behavior of repository save method
        when(historicoRepository.save(any())).thenReturn(new Historico(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ABC123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                "XYZ456",
                TipoTransacao.DEBITO,
                LocalDate.now(),
                BigDecimal.valueOf(100.00)
        ));

        // Calling the method under test
        HistoricoTransacaoResponse response = transacaoService.salvarTransacao(request);

        // Printing the response
        System.out.println("Resposta obtida:");
        System.out.println(response);

        // Verifying the returned response
        assertEquals("ABC123", response.chaveOrigem());
        assertEquals("XYZ456", response.chaveDestino());
        assertEquals(TipoTransacao.DEBITO, response.tipoTransacao());
        assertEquals(BigDecimal.valueOf(100.00), response.valor());
        assertEquals(LocalDate.now(), response.dataTransacao());

        // Verifying if the save method was called
        verify(historicoRepository, times(1)).save(any());
    }
}
