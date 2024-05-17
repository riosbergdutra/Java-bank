package com.bancaria.contabancaria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import com.bancaria.contabancaria.dtos.chave.ChaveDto;
import com.bancaria.contabancaria.dtos.deposito.request.DepositoRequestDto;
import com.bancaria.contabancaria.dtos.deposito.response.DepositoResponseDto;
import com.bancaria.contabancaria.dtos.transferencia.request.TransferenciaRequestDto;
import com.bancaria.contabancaria.dtos.transferencia.response.TransferenciaResponseDto;
import com.bancaria.contabancaria.services.BancariaService;

@RestController
@RequestMapping("/bancaria")
public class BancariaController {

    @Autowired
    private BancariaService bancariaService;

    //metodo post para adicionar uma chave na conta bancaria
    @PostMapping("/adicionarchave/{id}")
public ResponseEntity<ChaveDto> adicionarChave(@PathVariable UUID id, @RequestBody ChaveDto chaveDto) {
    ChaveDto response = bancariaService.adicionarChave(id, chaveDto);
    
    if (response != null) {
        return new ResponseEntity<>(chaveDto, HttpStatus.OK);
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
    //metodo post para fazer deposito de dinheiro
@PostMapping("/depositar")
    public ResponseEntity<DepositoResponseDto> depositar(@RequestBody DepositoRequestDto depositoRequestDto) {
        DepositoResponseDto responseDto = bancariaService.depositar(depositoRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    //metodo post para fazer transferencia de dinheiro
    @PostMapping("/transferencia")
    public ResponseEntity<TransferenciaResponseDto> realizarTransferencia(@RequestBody TransferenciaRequestDto transferenciaDto) {
        TransferenciaResponseDto responseDto = bancariaService.realizarTransferencia(transferenciaDto);
        return ResponseEntity.ok(responseDto);
    }
}
