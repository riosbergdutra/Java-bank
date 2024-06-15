package com.bancaria.contabancaria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import com.bancaria.contabancaria.dtos.chave.ChaveDto;
import com.bancaria.contabancaria.dtos.deposito.request.DepositoRequestDto;
import com.bancaria.contabancaria.dtos.deposito.response.DepositoResponseDto;
import com.bancaria.contabancaria.dtos.transferencia.request.TransacaoRequestDto;
import com.bancaria.contabancaria.dtos.transferencia.response.TransacaoResponseDto;
import com.bancaria.contabancaria.services.BancariaService;

@RestController
@RequestMapping("/bancaria")
public class BancariaController {

    @Autowired
    private BancariaService bancariaService;

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/adicionarchave/{id}")
    public ResponseEntity<ChaveDto> adicionarChave(@PathVariable UUID id, @RequestBody ChaveDto chaveDto, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        try {
            ChaveDto response = bancariaService.adicionarChave(id, userId, chaveDto);
            if (response != null) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/depositar")
    public ResponseEntity<DepositoResponseDto> depositar(@RequestBody DepositoRequestDto depositoRequestDto, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        try {
            DepositoResponseDto responseDto = bancariaService.depositar(userId, depositoRequestDto);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping("/transferencia")
    public ResponseEntity<TransacaoResponseDto> realizarTransacao(@RequestBody TransacaoRequestDto transferenciaDto, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        try {
            TransacaoResponseDto responseDto = bancariaService.realizarTransacao(userId, transferenciaDto);
            if (responseDto.sucesso()) {
                return ResponseEntity.ok(responseDto);
            } else {
                return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
