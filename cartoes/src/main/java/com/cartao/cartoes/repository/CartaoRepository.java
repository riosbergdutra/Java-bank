package com.cartao.cartoes.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cartao.cartoes.model.Cartao;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, UUID> {

    Optional<Cartao> findByIdBancaria(UUID idBancaria);

    boolean existsByNumeroCartao(String numeroConta);

    Boolean existsByCvv(String cvv);
}

