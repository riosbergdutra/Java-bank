package com.conta.usuarios.config;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.conta.usuarios.model.Usuario;
import com.conta.usuarios.repository.UsuarioRepository;
import com.conta.usuarios.enums.TipoConta;

@Configuration
public class AdminAccountInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            String adminEmail = "admin@admin.com";
            if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setId_usuario(UUID.randomUUID());
                admin.setNome("Admin");
                admin.setEmail(adminEmail);
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setCpf("00000000000");
                admin.setRoles(Set.of(Usuario.Role.ADMIN));
                admin.setTipoConta(TipoConta.CORRENTE);
                admin.setDataConta(LocalDate.now());

                usuarioRepository.save(admin);
                System.out.println("Admin account created.");
            } else {
                System.out.println("Admin account already exists.");
            }
        };
    }
}
