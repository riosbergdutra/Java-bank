package com.cartao.cartoes.dtos.pedircartao.req;



import com.cartao.cartoes.enums.EstadoBrasil;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PedirCartaoRequest(
    @NotEmpty @Size(min = 1, max = 100) String rua,
    @NotEmpty @Size(min = 1, max = 50) String cidade,
    @NotNull EstadoBrasil estado,
    @NotEmpty @Pattern(regexp = "\\d{5}-\\d{3}") String cep,
    @NotEmpty @Size(min = 1, max = 10) String numero,
    String complemento) {

}
