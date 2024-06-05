package com.conta.usuarios.model;



import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.conta.usuarios.dtos.req.LoginRequest;
import com.conta.usuarios.enums.TipoConta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario  {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private UUID id_usuario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Enumerated(EnumType.STRING)
    private Set<Role>  roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta")
    private TipoConta tipoConta;

    @Column(name = "data_conta")
    private LocalDate dataConta;

    public enum Role {
        ADMIN,
        USER;

        public String getName() {
            return this.name();
        }
    }
      public boolean isLoginCorrect(LoginRequest loginRequest, BCryptPasswordEncoder passwordEncoder) {
        return getEmail().equals(loginRequest.email()) && passwordEncoder.matches(loginRequest.senha(), getSenha());
    }

  

}
