package com.conta.usuarios.services;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;
import com.conta.usuarios.dtos.res.UsuarioResponseDto;

import java.util.List;
import java.util.UUID;

public interface UsuarioService {
    UsuarioResponseDto criarUsuarioService(UsuarioRequestDto usuarioDto);
    UsuarioResponseDto buscarUsuarioPorIdService(UUID id);
    List<UsuarioResponseDto> listarUsuariosService();
    UsuarioResponseDto atualizarUsuarioService(UUID id, UsuarioRequestDto usuarioDto);
    void deletarUsuarioService(UUID id);
}
