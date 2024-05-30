package com.historico.historicotransacao.services.impl;

import com.historico.historicotransacao.Enum.TipoTransacao;
import com.historico.historicotransacao.dtos.historico.req.HistoricoTransacaoRequest;
import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;
import com.historico.historicotransacao.model.Historico;
import com.historico.historicotransacao.repository.HistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Nested
    class ProcessarMensagemSQSTests {
        @Test
        void testProcessarMensagemSQS_ValidMessage() {
            String mensagemSQS = "Chave Origem: ABC123\n" +
                    "ID Bancaria Origem: 123e4567-e89b-12d3-a456-556642440000\n" +
                    "Chave Destino: XYZ456\n" +
                    "ID Usuario Origem: 123e4567-e89b-12d3-a456-556642440001\n" +
                    "ID Bancaria Destino: 123e4567-e89b-12d3-a456-556642440002\n" +
                    "ID Usuario Destino: 123e4567-e89b-12d3-a456-556642440003\n" +
                    "Tipo da Transacao: DEBITO\n" +
                    "Valor: 100.00\n" +
                    "data Transacao: 2024-05-25";

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

            transacaoService.processarMensagemSQS(mensagemSQS);

            verify(historicoRepository, times(1)).save(any());
        }
    }

    @Nested
    class SalvarTransacaoTests {
        @Test
        void testSalvarTransacao() {
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

            HistoricoTransacaoResponse response = transacaoService.salvarTransacao(request);

            assertEquals("ABC123", response.chaveOrigem());
            assertEquals("XYZ456", response.chaveDestino());
            assertEquals(TipoTransacao.DEBITO, response.tipoTransacao());
            assertEquals(BigDecimal.valueOf(100.00), response.valor());
            assertEquals(LocalDate.now(), response.dataTransacao());

            verify(historicoRepository, times(1)).save(any());
        }
    }

    @Nested
    class BuscarTransacaoPorIdTests {
        @Test
        void testBuscarTransacaoPorId_DeveRetornarTransacao_QuandoIdExistir() {
            UUID idTransacao = UUID.randomUUID();
            Historico historico = new Historico(
                    idTransacao,
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

            when(historicoRepository.findById(idTransacao)).thenReturn(Optional.of(historico));

            HistoricoTransacaoResponse response = transacaoService.buscarTransacaoPorId(idTransacao);

            assertNotNull(response);
            assertEquals("ABC123", response.chaveOrigem());
            assertEquals("XYZ456", response.chaveDestino());
            assertEquals(TipoTransacao.DEBITO, response.tipoTransacao());
            assertEquals(BigDecimal.valueOf(100.00), response.valor());
            assertEquals(LocalDate.now(), response.dataTransacao());

        }

        @Test
        void testBuscarTransacaoPorId_DeveLancarExcecao_QuandoIdNaoExistir() {
            UUID idTransacao = UUID.randomUUID();

            when(historicoRepository.findById(idTransacao)).thenReturn(Optional.empty());

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                transacaoService.buscarTransacaoPorId(idTransacao);
            });

            assertEquals("Transação não encontrada com o ID: " + idTransacao, thrown.getMessage());

        }
    }
}
