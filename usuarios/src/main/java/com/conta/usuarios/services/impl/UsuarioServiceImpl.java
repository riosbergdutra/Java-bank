package com.conta.usuarios.services.impl;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;
import com.conta.usuarios.dtos.res.UsuarioResponseDto;
import com.conta.usuarios.model.Usuario;
import com.conta.usuarios.repository.UsuarioRepository;
import com.conta.usuarios.services.UsuarioService;

import io.awspring.cloud.sqs.operations.SqsTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SqsTemplate sqsTemplate;

    
    @Override
    public UsuarioResponseDto criarUsuarioService(UsuarioRequestDto usuarioDto) {
        // Verifica se o usuário já existe pelo e-mail
        if (usuarioRepository.findByCpf(usuarioDto.cpf()) != null) {
            throw new RuntimeException("Usuário já existe!");
        }

        // Criptografa a senha antes de salvar no banco de dados
        String senhaHash = passwordEncoder.encode(usuarioDto.senha());

        // Define a data da conta como a data atual se não estiver definida no DTO de requisição
        Date dataConta = usuarioDto.dataConta() != null ? usuarioDto.dataConta() : (Date) Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Cria um novo usuário com base nos dados fornecidos no DTO de requisição
        Usuario novoUsuario = new Usuario(
                UUID.randomUUID(),
                usuarioDto.nome(),
                usuarioDto.email(),
                senhaHash,
                usuarioDto.cpf(),
                usuarioDto.dataNascimento(),
                usuarioDto.endereco(),
                usuarioDto.cep(),
                usuarioDto.role(),
                usuarioDto.tipoConta(),
                dataConta
        );

        // Salva o novo usuário no banco de dados
        Usuario usuarioCriado = usuarioRepository.save(novoUsuario);
        
 // Envie a mensagem para a fila SQS com texto simples
        String queueUrl = "http://localhost:4566/000000000000/usuarios";
        String messageBody = "id_usuario: " + usuarioCriado.getId_usuario() + "\n" +
              "tipo_conta: " + usuarioCriado.getTipoConta() + "\n" +
              "data_conta: " + usuarioCriado.getDataConta();
        sqsTemplate.send(queueUrl, messageBody);
        // Retorna um DTO de resposta com os dados do novo usuário
        return new UsuarioResponseDto(
            usuarioCriado.getNome(),
            usuarioCriado.getEmail(),
            usuarioCriado.getCpf(),
            usuarioCriado.getDataNascimento(), 
            usuarioCriado.getEndereco(),
            usuarioCriado.getTipoConta(),
            usuarioCriado.getDataConta()
    );      }
    @Override
    public UsuarioResponseDto buscarUsuarioPorIdService(UUID id) {
        // Busca um usuário pelo ID no banco de dados
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        // Verifica se o usuário foi encontrado
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            return new UsuarioResponseDto(
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getCpf(),
                    usuario.getDataNascimento(),
                    usuario.getEndereco(),
                    usuario.getTipoConta(),
                    usuario.getDataConta()
            );
        }
        return null;
    }

    @Override
    public List<UsuarioResponseDto> listarUsuariosService() {
        // Lista todos os usuários do banco de dados
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Converte a lista de usuários para uma lista de DTOs de resposta
        return usuarios.stream()
                .map(usuario -> new UsuarioResponseDto(
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getCpf(),
                        usuario.getDataNascimento(), usuario.getEndereco(),
                        usuario.getTipoConta(), usuario.getDataConta()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDto atualizarUsuarioService(UUID id, UsuarioRequestDto usuarioDto) {
        // Busca o usuário pelo ID no banco de dados
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        // Verifica se o usuário foi encontrado
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Atualiza os dados do usuário com base nos dados fornecidos no DTO de requisição
            usuario.setNome(usuarioDto.nome());
            usuario.setEmail(usuarioDto.email());
            String senhaHash = passwordEncoder.encode(usuarioDto.senha());
            usuario.setSenha(senhaHash);
            usuario.setCpf(usuarioDto.cpf());
            usuario.setDataNascimento(usuarioDto.dataNascimento());
            usuario.setEndereco(usuarioDto.endereco());
            usuario.setCep(usuarioDto.cep());
            usuario.setTipoConta(usuarioDto.tipoConta());
            usuario.setDataConta(usuarioDto.dataConta());

            // Salva as alterações no banco de dados
            usuarioRepository.save(usuario);

            // Retorna um DTO de resposta com os dados atualizados do usuário
            return new UsuarioResponseDto(
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getCpf(),
                    usuario.getDataNascimento(),
                    usuario.getEndereco(),
                    usuario.getTipoConta(),
                    usuario.getDataConta()
            );
        }
        return null;
    }

    @Override
    public void deletarUsuarioService(UUID id) {
        // Remove o usuário do banco de dados pelo ID
        usuarioRepository.deleteById(id);
    }
}
