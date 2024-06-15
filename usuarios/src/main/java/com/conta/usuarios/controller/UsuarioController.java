package com.conta.usuarios.controller;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;
import com.conta.usuarios.dtos.req.UsuarioSenhaReqDto;
import com.conta.usuarios.dtos.res.UsuarioResponseDto;
import com.conta.usuarios.dtos.res.UsuarioSenhaResDto;
import com.conta.usuarios.services.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/criar")
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@RequestBody UsuarioRequestDto usuarioDto) {
        UsuarioResponseDto responseDto = usuarioService.criarUsuarioService(usuarioDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UsuarioResponseDto responseDto = usuarioService.buscarUsuarioPorIdService(id, userId);
        if (responseDto != null) {
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios(Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<UsuarioResponseDto> usuarios = usuarioService.listarUsuariosService();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PutMapping("/senha/{id}")
    public ResponseEntity<UsuarioSenhaResDto> atualizarUsuarioSenha(@PathVariable UUID id,
            @RequestBody UsuarioSenhaReqDto usuarioDto, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UsuarioSenhaResDto responseDto = usuarioService.atualizarUsuarioSenhaService(id, userId, usuarioDto);
        if (responseDto != null) {
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        try {
            usuarioService.deletarUsuarioService(id, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}