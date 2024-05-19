package com.historico.historicotransacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.historico.historicotransacao.Enum.TipoTransacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "historico")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Historico {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, name = "id_transacao")
    private UUID idTransacao;

    @Column(name = "id_bancaria_origem")
    private UUID idBancariaOrigem;

    @Column(name = "id_usuario_origem")
    private UUID idUsuarioOrigem;

    @Column(name = "chave_origem", nullable = false)
    private String chaveOrigem;

    @Column(name = "id_bancaria_destino")
    private UUID idBancariaDestino;

    @Column(name = "id_usuario_destino")
    private UUID idUsuarioDestino;

    @Column(name = "chave_destino", nullable = false)
    private UUID idChaveDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao", nullable = false)
    private TipoTransacao tipoTransacao;

    @Column(name = "data_transacao", nullable = false)
    private LocalDate dataTransacao;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

}
