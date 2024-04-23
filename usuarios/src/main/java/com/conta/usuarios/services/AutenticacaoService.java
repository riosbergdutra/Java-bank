package com.conta.usuarios.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.conta.usuarios.dtos.AuthDto;

public interface AutenticacaoService extends UserDetailsService {
    public String obterToken(AuthDto authDto);
    public String validaTokenJWT(String token);
}
