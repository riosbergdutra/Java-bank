package com.cartao.cartoes.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cartao.cartoes.dtos.criarcartao.req.CriarCartaoRequest;
import com.cartao.cartoes.dtos.criarcartao.res.CriarCartaoResponse;
import com.cartao.cartoes.enums.EnumCartao;
import com.cartao.cartoes.enums.StatusCartao;
import com.cartao.cartoes.model.Cartao;
import com.cartao.cartoes.repository.CartaoRepository;

public class CartaoServiceTest {

    @InjectMocks
    private CartaoService cartaoService;

    @Mock
    private CartaoRepository cartaoRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessarMensagemSQS_Sucesso() {
        String mensagemSQS = "ID da Conta Bancária: " + UUID.randomUUID().toString() + "\n" +
                             "ID do Usuário: " + UUID.randomUUID().toString() + "\n" +
                             "Saldo: 1000.00\n" +

                             when(cartaoRepository.existsByCvv(any(String.class))).thenReturn(false);
        CriarCartaoResponse response = cartaoService.processarMensagemSQSCriarCartao(mensagemSQS);

        assertTrue(response.sucesso(), "A operação falhou. Mensagem: " + response.mensagem());
        assertEquals("Cartão criado com sucesso.", response.mensagem());
    }

    @Test
    public void testProcessarMensagemSQS_MensagemIncompleta() {
        String mensagemSQS = "ID da Conta Bancária: " + UUID.randomUUID().toString() + "\n" +
                             "ID do Usuário: " + UUID.randomUUID().toString();

        CriarCartaoResponse response = cartaoService.processarMensagemSQSCriarCartao(mensagemSQS);

        assertFalse(response.sucesso());
        assertEquals("Mensagem incompleta ou inválida.", response.mensagem());
    }

    @Test
    public void testCriarCartaoService() {
        UUID idUsuario = UUID.randomUUID();
        UUID idBancaria = UUID.randomUUID();
        String cvv = "123";
        BigDecimal saldo = new BigDecimal("1000.00");

        String numeroCartao = cartaoService.gerarNumeroDeCartao(); // Gerar número de cartão único

        CriarCartaoRequest request = new CriarCartaoRequest(
            null, idBancaria, idUsuario, cvv, numeroCartao, StatusCartao.BLOQUEADO, null, EnumCartao.BLOQUEADO, EnumCartao.BLOQUEADO, saldo
        );

        Cartao cartao = new Cartao(UUID.randomUUID(), idUsuario, idBancaria, cvv, numeroCartao, StatusCartao.BLOQUEADO, null, EnumCartao.BLOQUEADO, EnumCartao.BLOQUEADO, saldo);

        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);

        cartaoService.criarCartaoService(request, cvv);
    }
}
