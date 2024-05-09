package com.bancaria.contabancaria.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.bancaria.contabancaria.enums.TipoConta;

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
@Table(name = "bancaria")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, name = "id_bancaria")
    private UUID idBancaria;

    @Column(unique = true, name = "id_usuario")
    private UUID idUsuario;

     @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta")
    private TipoConta tipoConta;

    private BigDecimal saldo;

    @Column(name = "data_conta")
    private LocalDate dataConta;

    
}
