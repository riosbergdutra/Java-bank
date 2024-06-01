package com.cartao.cartoes.dtos.criarcartao.req;

import java.math.BigDecimal;
import java.util.UUID;

import com.cartao.cartoes.enums.EnumCartao;
import com.cartao.cartoes.enums.StatusCartao;

public record CriarCartaoRequest(
 UUID idCartao,
 UUID idUsuario,
 UUID idBancaria,
 String cvv,
 String numeroCartao,
 StatusCartao statusCartao,
 String senha,
 EnumCartao debito,
 EnumCartao credito,
 BigDecimal saldoBancaria
)
{

}
