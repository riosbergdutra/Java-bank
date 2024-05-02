package com.conta.usuarios.services.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.conta.usuarios.dtos.AuthDto;
import com.conta.usuarios.model.Usuario;
import com.conta.usuarios.repository.UsuarioRepository;
import com.conta.usuarios.services.AutenticacaoService;

@Service
public class AutenticacaoServiceImpl implements AutenticacaoService  { // Implementar a interface correta
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }
        return usuario;
    }

    @Override
    public String obterToken(AuthDto authDto) {
        Usuario usuario = usuarioRepository.findByEmail(authDto.email());
        return geraTokenJwt(usuario);
    }

    public String geraTokenJwt(Usuario usuario) {
        try {
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuário não encontrado");
            }
            Algorithm algorithm = Algorithm.HMAC256("my-secret");
            return JWT.create().withIssuer("auth-api").withSubject(usuario.getCpf()).withExpiresAt(geraDataExpiracao()).sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao tentar gerar o token" + exception.getMessage());
        }
    }
    

    public String validaTokenJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("my-secret");
            return JWT.require(algorithm).withIssuer("auth-api").build().verify(token).getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant geraDataExpiracao() {
        return LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("-03:00"));
    }
}
