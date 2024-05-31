package com.historico.historicotransacao.controllers;

import com.historico.historicotransacao.Enum.TipoTransacao;
import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;
import com.historico.historicotransacao.services.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransacaoControllerTest {

    @Mock
    private TransacaoService transacaoService;

    @InjectMocks
    private TransacaoController transacaoController;

    @SuppressWarnings("deprecation")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    class BuscarTransacaoPorIdTests {
        @Test
void testBuscarTransacaoPorId_DeveRetornarTransacao_QuandoIdExistir() {
    UUID idTransacao = UUID.randomUUID();
    HistoricoTransacaoResponse historicoResponseMock = new HistoricoTransacaoResponse(
            TipoTransacao.DEBITO, // Ajuste aqui para o Enum correspondente
            BigDecimal.valueOf(100.00),
            LocalDate.now()
    );

    when(transacaoService.buscarTransacaoPorId(idTransacao)).thenReturn(historicoResponseMock);

    ResponseEntity<HistoricoTransacaoResponse> responseEntity = transacaoController.buscarTransacaoPorId(idTransacao);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(historicoResponseMock, responseEntity.getBody());
    verify(transacaoService, times(1)).buscarTransacaoPorId(idTransacao);
    System.out.println(" 'testBuscarTransacaoPorId_DeveRetornarTransacao_QuandoIdExistir' concluído com sucesso!");
}


        @Test
        void testBuscarTransacaoPorId_DeveRetornarErro_QuandoIdNaoExistir() {
            UUID idTransacao = UUID.randomUUID();

            when(transacaoService.buscarTransacaoPorId(idTransacao)).thenThrow(new RuntimeException("Transação não encontrada com o ID:"  + idTransacao));

            ResponseEntity<HistoricoTransacaoResponse> responseEntity = transacaoController.buscarTransacaoPorId(idTransacao);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            verify(transacaoService, times(1)).buscarTransacaoPorId(idTransacao);
            System.out.println("'testBuscarTransacaoPorId_DeveRetornarErro_QuandoIdNaoExistir' concluido com sucesso");
        }
    }
}
