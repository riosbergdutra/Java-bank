package com.conta.usuarios.model;



import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.conta.usuarios.enums.RoleEnum;
import com.conta.usuarios.enums.TipoConta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id_usuario")
public class Usuario implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id 
    public Long id_usuario;

    @Column(nullable = false)
    public String nome;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    public String senha;

    @Column(nullable = false)
    public String cpf;

    @Column(nullable = false, name= "data_nascimento")
    public Date dataNascimento;

    @Column(nullable = false)
    public String endereco;

    @Column(nullable = false)
    public String cep;

    @Column(nullable = false)
    public RoleEnum role;

    @Column(name = "tipo_conta")
    public TipoConta tipoConta;

    @Column(name = "data_conta")
    public Date dataConta;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == RoleEnum.ADMIN) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
            );
        }
        return List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );
    }
    @Override
    public String getPassword() {
       return this.senha;
    }
    @Override
    public String getUsername() {
       return this.cpf;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

}
