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
            String email = autenticacaoService.validaTokenJWT(token);
            Usuario usuario = usuarioRepository.findByEmail(email);

            if (usuario != null) {
                var authorities = usuario.getAuthorities();
                String username = usuario.getUsername();
                
                // Log das informações do usuário
                System.out.println("Usuário autenticado: " + username);
                System.out.println("Autoridades do usuário: " + authorities);

                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Usuário não encontrado para email: " + email);
            }
        }

        // Continua a execução da cadeia de filtros
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
