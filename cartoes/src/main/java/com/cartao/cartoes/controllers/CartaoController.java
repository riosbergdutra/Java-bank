package com.cartao.cartoes.controllers;
import com.cartao.cartoes.dtos.ativarcartao.req.AtivarCartaoRequest;
import com.cartao.cartoes.dtos.criarcartao.res.CriarCartaoResponse;
import com.cartao.cartoes.dtos.pedircartao.req.PedirCartaoRequest;
import com.cartao.cartoes.dtos.pedircartao.res.PedirCartaoResponse;
import com.cartao.cartoes.services.CartaoService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    // Pedido de entrega de cartão
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/pedir-cartao/{id}")
    public ResponseEntity<PedirCartaoResponse> pedirEntregaCartao(@Valid @RequestBody PedirCartaoRequest pedirCartaoRequest,
            @PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        // Prossiga com o pedido de entrega do cartão
        try {
            PedirCartaoResponse response = cartaoService.pedirEntregaCartao(pedirCartaoRequest, id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PedirCartaoResponse(false,
                    "Erro ao enviar pedido de entrega do cartão: " + e.getMessage()));
        }
    }

    // Ativação de cartão
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/ativar-cartao/{id}")
    public ResponseEntity<CriarCartaoResponse> ativarCartao(@PathVariable UUID id,
            @RequestBody AtivarCartaoRequest request, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // Verificar se o escopo do token JWT é adequado para acessar este recurso e se o ID do usuário é correspondente

        // Prossiga com a ativação do cartão
        try {
            CriarCartaoResponse response = cartaoService.ativarCartao(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CriarCartaoResponse(false,
                    "Erro ao ativar cartão: " + e.getMessage()));
        }
    }
}
