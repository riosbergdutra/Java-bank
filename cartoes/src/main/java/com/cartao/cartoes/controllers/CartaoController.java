package com.cartao.cartoes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cartao.cartoes.dtos.pedircartao.req.PedirCartaoRequest;
import com.cartao.cartoes.dtos.pedircartao.res.PedirCartaoResponse;
import com.cartao.cartoes.services.CartaoService;

@RestController
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;
    
     @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/pedir-cartao")
    public ResponseEntity<PedirCartaoResponse> pedirEntregaCartao(@RequestBody PedirCartaoRequest pedirCartaoRequest) {
        try {
            PedirCartaoResponse response = cartaoService.pedirEntregaCartao(pedirCartaoRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PedirCartaoResponse(false, "Erro ao enviar pedido de entrega do cartão: " + e.getMessage()));
        }
    }
}