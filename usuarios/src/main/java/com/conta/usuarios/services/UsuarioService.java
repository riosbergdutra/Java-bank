package com.conta.usuarios.services;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;
import com.conta.usuarios.dtos.res.UsuarioResponseDto;

import java.util.List;

public interface UsuarioService {
    UsuarioResponseDto criarUsuarioService(UsuarioRequestDto usuarioDto);
    UsuarioResponseDto buscarUsuarioPorIdService(Long id);
    List<UsuarioResponseDto> listarUsuariosService();
    UsuarioResponseDto atualizarUsuarioService(Long id, UsuarioRequestDto usuarioDto);
    void deletarUsuarioService(Long id);
}
