package com.historico.historicotransacao.controllers;

import com.historico.historicotransacao.dtos.historico.res.HistoricoTransacaoResponse;
import com.historico.historicotransacao.services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/historico")
public class TransacaoController {

    private final TransacaoService transacaoService;

    @Autowired
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping("/transacoes/{id}")
    public ResponseEntity<HistoricoTransacaoResponse> buscarTransacaoPorId(@PathVariable UUID id) {
        try {
            HistoricoTransacaoResponse response = transacaoService.buscarTransacaoPorId(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Erro ao buscar transação por ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
