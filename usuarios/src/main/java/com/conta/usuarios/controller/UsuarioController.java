package com.conta.usuarios.controller;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;
import com.conta.usuarios.dtos.req.UsuarioSenhaReqDto;
import com.conta.usuarios.dtos.res.UsuarioResponseDto;
import com.conta.usuarios.dtos.res.UsuarioSenhaResDto;
import com.conta.usuarios.services.UsuarioService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable UUID id) {
        UsuarioResponseDto responseDto = usuarioService.buscarUsuarioPorIdService(id);
        if (responseDto != null) {
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        List<UsuarioResponseDto> usuarios = usuarioService.listarUsuariosService();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @PutMapping("/senha/{id}")
public ResponseEntity<UsuarioSenhaResDto> atualizarUsuarioSenha(@PathVariable UUID id, @RequestBody UsuarioSenhaReqDto usuarioDto) {
    UsuarioSenhaResDto responseDto = usuarioService.atualizarUsuarioSenhaService(id, usuarioDto);
    if (responseDto != null) {
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id) {
        usuarioService.deletarUsuarioService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
