package com.bancaria.contabancaria.services;

import java.util.UUID;

import com.bancaria.contabancaria.dtos.bancaria.request.BancariaRequestDto;
import com.bancaria.contabancaria.dtos.bancaria.response.BancariaResponseDto;
import com.bancaria.contabancaria.dtos.chave.ChaveDto;
import com.bancaria.contabancaria.dtos.deposito.request.DepositoRequestDto;
import com.bancaria.contabancaria.dtos.deposito.response.DepositoResponseDto;
import com.bancaria.contabancaria.dtos.transferencia.request.TransacaoRequestDto;
import com.bancaria.contabancaria.dtos.transferencia.response.TransacaoResponseDto;


public interface BancariaService {

    //realiza transferencia pelas chaves
    TransacaoResponseDto realizarTransacao(TransacaoRequestDto transferenciaDto);

    // recebe mensagem sqs e cria a bancaria para o usuario id
    BancariaResponseDto criarBancariaService(BancariaRequestDto bancariaDto);

    //adiciona uma chave para permitir fazer transferencia e etc...
    ChaveDto adicionarChave(UUID id, ChaveDto chaveDto);
    
    DepositoResponseDto depositar(DepositoRequestDto depositoRequestDto);

    // recebe mensagem sqs e deleta a bancaria para o usuario id
    void deletarBancariaService(UUID idUsuario);
    // recebe mensagem sqs e decide oque fará em seguida(criar conta ou deletar a conta)
    void processarMensagemSQS(String mensagemSQS);
}
