package com.conta.usuarios.services;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;
import com.conta.usuarios.dtos.req.UsuarioSenhaReqDto;
import com.conta.usuarios.dtos.res.UsuarioResponseDto;
import com.conta.usuarios.dtos.res.UsuarioSenhaResDto;

import java.util.List;
import java.util.UUID;

public interface UsuarioService {
    UsuarioResponseDto criarUsuarioService(UsuarioRequestDto usuarioDto);
    UsuarioResponseDto buscarUsuarioPorIdService(UUID id);
    List<UsuarioResponseDto> listarUsuariosService();
    UsuarioSenhaResDto atualizarUsuarioSenhaService(UUID id, UsuarioSenhaReqDto usuarioDto);
    void deletarUsuarioService(UUID id);
}
