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

   // Buscar usuário por ID
   @PreAuthorize("hasAuthority('SCOPE_USER')")
   @GetMapping("/{id}")
   public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable UUID id, Authentication authentication) {
       UUID userId = UUID.fromString(authentication.getName());
       // Verificar se o escopo do token JWT é adequado para acessar este recurso e se o ID do usuário é correspondente
       if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_USER")) || !userId.equals(id)) {
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
       }

       // Prossiga com a busca do usuário por ID
       UsuarioResponseDto responseDto = usuarioService.buscarUsuarioPorIdService(id);
       if (responseDto != null) {
           return new ResponseEntity<>(responseDto, HttpStatus.OK);
       } else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
   }

   // Listar usuários
   @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
   @GetMapping("/listar")
   public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios(Authentication authentication) {
       // Verificar se o escopo do token JWT é adequado para acessar este recurso
       if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_ADMIN"))) {
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
       }

       // Prossiga com a listagem de usuários
       List<UsuarioResponseDto> usuarios = usuarioService.listarUsuariosService();
       return new ResponseEntity<>(usuarios, HttpStatus.OK);
   }

   // Atualizar senha do usuário
   @PreAuthorize("hasAuthority('SCOPE_USER')")
   @PutMapping("/senha/{id}")
   public ResponseEntity<UsuarioSenhaResDto> atualizarUsuarioSenha(@PathVariable UUID id, @RequestBody UsuarioSenhaReqDto usuarioDto, Authentication authentication) {
       UUID userId = UUID.fromString(authentication.getName());
       // Verificar se o escopo do token JWT é adequado para acessar este recurso e se o ID do usuário é correspondente
       if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_USER")) || !userId.equals(id)) {
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
       }

       // Prossiga com a atualização da senha do usuário
       UsuarioSenhaResDto responseDto = usuarioService.atualizarUsuarioSenhaService(id, usuarioDto);
       if (responseDto != null) {
           return new ResponseEntity<>(responseDto, HttpStatus.OK);
       } else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
   }

   // Deletar usuário
   @PreAuthorize("hasAuthority('SCOPE_USER')")
   @DeleteMapping("/deletar/{id}")
   public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id, Authentication authentication) {
       UUID userId = UUID.fromString(authentication.getName());
       // Verificar se o escopo do token JWT é adequado para acessar este recurso e se o ID do usuário é correspondente
       if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_USER")) || !userId.equals(id)) {
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
       }

       // Prossiga com a exclusão do usuário
       usuarioService.deletarUsuarioService(id);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
   }
}

