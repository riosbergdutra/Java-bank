package com.historico.historicotransacao.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.historico.historicotransacao.model.Historico;

public interface HistoricoRepository extends JpaRepository<Historico, UUID> {

    List<Historico> findAllByIdUsuarioOrigemOrIdUsuarioDestino(UUID idUsuario, UUID idUsuario2);
}
