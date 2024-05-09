package com.bancaria.contabancaria.services;

import java.util.UUID;

import com.bancaria.contabancaria.dtos.req.BancariaRequestDto;
import com.bancaria.contabancaria.dtos.res.BancariaResponseDto;

public interface BancariaService {
    BancariaResponseDto criarBancariaService(BancariaRequestDto bancariaDto);
    void deletarBancariaService(UUID idUsuario);
    void processarMensagemSQS(String mensagemSQS);
}
