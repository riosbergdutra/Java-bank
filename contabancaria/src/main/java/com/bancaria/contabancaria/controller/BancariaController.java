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

import java.util.Optional;
import java.util.UUID;

import com.bancaria.contabancaria.dtos.chave.ChaveDto;
import com.bancaria.contabancaria.dtos.deposito.request.DepositoRequestDto;
import com.bancaria.contabancaria.dtos.deposito.response.DepositoResponseDto;
import com.bancaria.contabancaria.dtos.transferencia.request.TransacaoRequestDto;
import com.bancaria.contabancaria.dtos.transferencia.response.TransacaoResponseDto;
import com.bancaria.contabancaria.model.Bancaria;
import com.bancaria.contabancaria.repository.BancariaRepository;
import com.bancaria.contabancaria.services.BancariaService;

@RestController
@RequestMapping("/bancaria")
public class BancariaController {

    @Autowired
    private BancariaService bancariaService;

    @Autowired
    private BancariaRepository bancariaRepository;

@PreAuthorize("hasAuthority('SCOPE_USER')")
@PostMapping("/adicionarchave/{id}")
public ResponseEntity<ChaveDto> adicionarChave(@PathVariable UUID id, @RequestBody ChaveDto chaveDto, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());
    Optional<Bancaria> optionalBancaria = bancariaRepository.findById(id);
    if (optionalBancaria.isPresent()) {
        Bancaria bancaria = optionalBancaria.get();
        if (!bancaria.getIdUsuario().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    ChaveDto response = bancariaService.adicionarChave(id, chaveDto);

    if (response != null) {
        return new ResponseEntity<>(chaveDto, HttpStatus.OK);
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


  // método post para fazer depósito de dinheiro
@PreAuthorize("hasAuthority('SCOPE_USER')")
@PostMapping("/depositar")
public ResponseEntity<DepositoResponseDto> depositar(@RequestBody DepositoRequestDto depositoRequestDto, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());
    Optional<Bancaria> optionalBancaria = bancariaRepository.findByIdUsuario(userId);
    // Verificar se o escopo do token JWT é adequado para acessar este recurso
    if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_USER")) || !optionalBancaria.isPresent()) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Se o escopo for válido e a conta bancária existir para o usuário, prosseguir com a lógica de depósito
    DepositoResponseDto responseDto = bancariaService.depositar(depositoRequestDto);
    return ResponseEntity.ok(responseDto);
}

// método post para fazer transferência de dinheiro
@PreAuthorize("hasAuthority('SCOPE_USER')")
@PostMapping("/transferencia")
public ResponseEntity<TransacaoResponseDto> realizarTransacao(@RequestBody TransacaoRequestDto transferenciaDto, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());
    Optional<Bancaria> optionalBancaria = bancariaRepository.findByIdUsuario(userId);
    // Verificar se o escopo do token JWT é adequado para acessar este recurso
    if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_USER")) || !optionalBancaria.isPresent()) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Se o escopo for válido e a conta bancária existir para o usuário, prosseguir com a lógica de transferência
    TransacaoResponseDto responseDto = bancariaService.realizarTransacao(transferenciaDto);
    return ResponseEntity.ok(responseDto);
}

}