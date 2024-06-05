package com.conta.usuarios.services;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.conta.usuarios.model.Usuario;


@Service
public class JwtService  { // Implementar a interface correta
    
    private final JwtEncoder jwtEncoder;
    private final long expiryDuration = 300L; // 5 minutes

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(Usuario user) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getId_usuario().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiryDuration))
                .claim("scope", String.join(" ", user.getRoles().stream().map(r -> r.getName()).toArray(String[]::new)))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long getExpiryDuration() {
        return expiryDuration;
    }
}
