package com.cartao.cartoes.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cartao.cartoes.dtos.ativarcartao.req.AtivarCartaoRequest;
import com.cartao.cartoes.dtos.criarcartao.res.CriarCartaoResponse;
import com.cartao.cartoes.dtos.pedircartao.req.PedirCartaoRequest;
import com.cartao.cartoes.dtos.pedircartao.res.PedirCartaoResponse;
import com.cartao.cartoes.services.CartaoService;

@RestController
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/pedir-cartao/{id}")
    public ResponseEntity<PedirCartaoResponse> pedirEntregaCartao(@RequestBody PedirCartaoRequest pedirCartaoRequest,
            @PathVariable UUID id) {
        try {
            PedirCartaoResponse response = cartaoService.pedirEntregaCartao(pedirCartaoRequest,id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PedirCartaoResponse(false,
                    "Erro ao enviar pedido de entrega do cartão: The given id must not be null"));
        }
    }
    
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/ativar-cartao/{id}")
    public ResponseEntity<CriarCartaoResponse> ativarCartao(@PathVariable UUID id, @RequestBody AtivarCartaoRequest request) {
        try {
            CriarCartaoResponse response = cartaoService.ativarCartao(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CriarCartaoResponse(false,
                    "Erro ao ativar cartão: " + e.getMessage()));
        }
    }
}
