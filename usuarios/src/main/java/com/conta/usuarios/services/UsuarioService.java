package com.conta.usuarios.services;

import com.conta.usuarios.dtos.req.UsuarioRequestDto;
import com.conta.usuarios.dtos.req.UsuarioSenhaReqDto;
import com.conta.usuarios.dtos.res.UsuarioResponseDto;
import com.conta.usuarios.dtos.res.UsuarioSenhaResDto;
import com.conta.usuarios.model.Usuario;
import com.conta.usuarios.repository.UsuarioRepository;

import io.awspring.cloud.sqs.operations.SqsTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SqsTemplate sqsTemplate;

    public UsuarioResponseDto criarUsuarioService(UsuarioRequestDto usuarioDto) {
        Set<Usuario.Role> roles = Set.of(Usuario.Role.USER);
        // Obtém a data atual
        LocalDate dataAtual = LocalDate.now();
        // se não for fornecida pelo dto gera uma data atual
        LocalDate dataConta = usuarioDto.dataConta() != null ? usuarioDto.dataConta() : dataAtual;
        
        // Cria um novo usuário com base nos dados fornecidos no DTO de requisição
        Usuario novoUsuario = new Usuario();
        novoUsuario.setId_usuario(UUID.randomUUID());
        novoUsuario.setNome(usuarioDto.nome());
        novoUsuario.setEmail(usuarioDto.email());
        novoUsuario.setSenha(passwordEncoder.encode(usuarioDto.senha()));
        novoUsuario.setCpf(usuarioDto.cpf());
        novoUsuario.setRoles(roles);
        novoUsuario.setTipoConta(usuarioDto.tipoConta());
        novoUsuario.setDataConta(dataConta);

        // Salva o novo usuário no banco de dados
        Usuario usuarioCriado = usuarioRepository.save(novoUsuario);

        // Envie a mensagem para a fila SQS
        String queueUrl = "http://localhost:4566/000000000000/usuarios";
        String messageBody = "idUsuario: " + usuarioCriado.getId_usuario() + "\n" +
                "tipoConta: " + usuarioCriado.getTipoConta() + "\n" +
                "dataConta: " + usuarioCriado.getDataConta() + "\n" +
                "acao: criar";

        sqsTemplate.send(queueUrl, messageBody);

        // Retorna um DTO de resposta com os dados do novo usuário
        return new UsuarioResponseDto(
                usuarioCriado.getNome(),
                usuarioCriado.getEmail(),
                usuarioCriado.getCpf(),
                usuarioCriado.getTipoConta(),
                usuarioCriado.getDataConta()
        );
    }

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
                    usuario.getTipoConta(),
                    usuario.getDataConta()
            );
        }
        return null;
    }

    public List<UsuarioResponseDto> listarUsuariosService() {
        // Lista todos os usuários do banco de dados
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Converte a lista de usuários para uma lista de DTOs de resposta
        return usuarios.stream()
                .map(usuario -> new UsuarioResponseDto(
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getCpf(),
                        usuario.getTipoConta(),
                        usuario.getDataConta()
                ))
                .collect(Collectors.toList());
    }

    public UsuarioSenhaResDto atualizarUsuarioSenhaService(UUID id, UsuarioSenhaReqDto usuarioDto) {
        // Busca o usuário pelo ID no banco de dados
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        // Verifica se o usuário foi encontrado
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Atualiza os dados do usuário com base nos dados fornecidos no DTO de requisição
            String senhaHash = passwordEncoder.encode(usuarioDto.senha());
            usuario.setSenha(senhaHash);

            // Salva as alterações no banco de dados
            usuarioRepository.save(usuario);

            // Retorna um DTO de resposta com os dados atualizados do usuário
            return new UsuarioSenhaResDto("senha mudada com sucesso");
        }
        return null;
    }

    public void deletarUsuarioService(UUID id) {
        try {
            // Busca o usuário pelo ID no banco de dados
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

            // Verifica se o usuário foi encontrado
            if (optionalUsuario.isPresent()) {
                Usuario usuario = optionalUsuario.get();

                // Envia a mensagem para a fila SQS com os dados do usuário
                String queueUrl = "http://localhost:4566/000000000000/usuarios";
                String messageBody = "idUsuario: " + usuario.getId_usuario() + "\n" +
                        "acao: deletar";

                sqsTemplate.send(queueUrl, messageBody);

                // Remove o usuário do banco de dados pelo ID
                usuarioRepository.deleteById(id);
            } else {
                System.out.println("Usuário não encontrado para o ID fornecido.");
            }
        } catch (Exception e) {
            // Tratar exceções
            e.printStackTrace();
        }
    }
    public Optional<Usuario> loginService(String email) {
        return (usuarioRepository.findByEmail(email));
    }
    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
