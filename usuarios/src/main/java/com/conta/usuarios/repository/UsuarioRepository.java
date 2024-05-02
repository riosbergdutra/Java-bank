package com.conta.usuarios.repository;



import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.conta.usuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,UUID> {
    Usuario findByEmail(String email);
} 
