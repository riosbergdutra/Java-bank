package com.cartao.cartoes.dtos.pedircartao.req;

import java.util.UUID;

import com.cartao.cartoes.enums.EstadoBrasil;

public record PedirCartaoRequest(UUID idCartao, UUID idUsuario,
        String rua,
        String cidade,
        EstadoBrasil estado,
        String cep,
        String numero,
        String complemento) {

}
