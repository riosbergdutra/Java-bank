package com.historico.historicotransacao.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.historico.historicotransacao.model.Historico;

public interface HistoricoRepository extends JpaRepository<Historico, UUID> {
}
