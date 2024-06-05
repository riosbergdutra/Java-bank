package com.conta.usuarios.controller;
import com.conta.usuarios.dtos.req.LoginRequest;
import com.conta.usuarios.dtos.res.LoginResponseDTO;
import com.conta.usuarios.services.JwtService;
import com.conta.usuarios.services.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public TokenController(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequest loginRequest) {
        var user = usuarioService.loginService(loginRequest.email());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest,usuarioService.getPasswordEncoder())) {
            throw new BadCredentialsException("User or password is invalid!");
        }

        var jwt = jwtService.generateToken(user.get());

        return ResponseEntity.ok(new LoginResponseDTO(jwt, jwtService.getExpiryDuration()));
    }
}
