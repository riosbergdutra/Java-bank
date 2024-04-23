package com.conta.usuarios.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.conta.usuarios.model.Usuario;
import com.conta.usuarios.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private com.conta.usuarios.services.AutenticacaoService autenticacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extraiTokenHeader(request);

        if (token != null) {
            String cpf = autenticacaoService.validaTokenJWT(token);
            Usuario usuario = usuarioRepository.findByCpf(cpf);

            var autentication = new UsernamePasswordAuthenticationToken(usuario, null,usuario.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(autentication);
        }
        filterChain.doFilter(request, response);
    }

    public String extraiTokenHeader(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

    if (authHeader == null) {
        return null;
    }
    if (!authHeader.split(" ")[0].equals("Bearer")) {
        return null;
    }
    return authHeader.split(" ")[1];
    }
    
}