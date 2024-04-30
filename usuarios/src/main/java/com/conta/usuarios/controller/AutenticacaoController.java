package com.conta.usuarios.controller;
import com.conta.usuarios.dtos.AuthDto;
import com.conta.usuarios.dtos.res.LoginResponseDTO;
import com.conta.usuarios.services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthDto authDto) {
        String token = autenticacaoService.obterToken(authDto);
        if (token != null) {
            return new ResponseEntity<>(new LoginResponseDTO(token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/validar")
    public ResponseEntity<Void> validarToken(@RequestBody String token) {
        String cpf = autenticacaoService.validaTokenJWT(token);
        if (!cpf.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
