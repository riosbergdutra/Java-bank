package com.cartao.cartoes.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cartao.cartoes.model.Cartao;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, UUID> {
    boolean existsByCvv(int cvv);
}

