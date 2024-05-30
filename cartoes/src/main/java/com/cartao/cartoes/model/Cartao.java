package com.cartao.cartoes.model;

import java.math.BigDecimal;
import java.util.UUID;

import com.cartao.cartoes.enums.EnumCartao;
import com.cartao.cartoes.enums.StatusCartao;

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

@Getter
@Setter
@Entity
@Table(name = "cartoes")
@AllArgsConstructor
@NoArgsConstructor
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, name = "id_cartao")
    private UUID idCartao;

    @Column(unique = true, name = "id_usuario")
    private UUID idUsuario;

    @Column(unique = true, name = "id_bancaria")
    private UUID idBancaria;

    @Column(name = "cvv")
    private int cvv;

    @Column(name = "status_cartao")
    @Enumerated(EnumType.STRING)
    private StatusCartao statusCartao;

    @Column(name = "senha")
    private String senha;

    @Column(name = "debito")
    @Enumerated(EnumType.STRING)
    private EnumCartao debito;

    @Column(name = "credito")
    @Enumerated(EnumType.STRING)
    private EnumCartao credito;

    @Column(name = "saldo_bancaria")
    private BigDecimal saldoBancaria;

}
