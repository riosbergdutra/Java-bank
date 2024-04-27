package com.conta.usuarios.services;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;


public interface UsuarioCacheService {
 void cacheUsuario(String key, UsuarioRequestDto usuarioDto);
 UsuarioRequestDto getUsuarioFromCache(String id_usuario);
 void removeUsuarioFromCache(String id_usuario);
void updateUsuarioInCache(String key, UsuarioRequestDto usuarioDto);
}

