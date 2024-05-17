package com.bancaria.contabancaria.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bancaria.contabancaria.model.Bancaria;

@Repository
public interface BancariaRepository extends JpaRepository<Bancaria, UUID> {

    Optional<Bancaria> findByIdUsuario(UUID idUsuario);
    
    Optional<Bancaria> findByChave(String chave);

}
