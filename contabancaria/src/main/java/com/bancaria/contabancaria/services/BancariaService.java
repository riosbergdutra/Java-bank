package com.bancaria.contabancaria.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bancaria.contabancaria.dtos.bancaria.request.BancariaRequestDto;
import com.bancaria.contabancaria.dtos.bancaria.response.BancariaResponseDto;
import com.bancaria.contabancaria.dtos.bancariasusuario.res.ContaBancariaResponse;
import com.bancaria.contabancaria.dtos.chave.ChaveDto;
import com.bancaria.contabancaria.dtos.deposito.request.DepositoRequestDto;
import com.bancaria.contabancaria.dtos.deposito.response.DepositoResponseDto;
import com.bancaria.contabancaria.dtos.transferencia.request.TransacaoRequestDto;
import com.bancaria.contabancaria.dtos.transferencia.response.TransacaoResponseDto;
import com.bancaria.contabancaria.enums.TipoConta;
import com.bancaria.contabancaria.model.Bancaria;
import com.bancaria.contabancaria.repository.BancariaRepository;
import com.bancaria.contabancaria.services.BancariaService;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@Service
public class BancariaService {

    @Autowired
    private BancariaRepository bancariaRepository;

    @Autowired
    private SqsTemplate sqsTemplate;

    public TransacaoResponseDto realizarTransacao(UUID userId, TransacaoRequestDto transferenciaDto) {
        Optional<Bancaria> optionalContaOrigem = bancariaRepository.findByChave(transferenciaDto.ChaveOrigem());
        Optional<Bancaria> optionalContaDestino = bancariaRepository.findByChave(transferenciaDto.ChaveDestino());

        if (optionalContaOrigem.isPresent() && optionalContaDestino.isPresent()) {
            Bancaria contaOrigem = optionalContaOrigem.get();
            Bancaria contaDestino = optionalContaDestino.get();

            if (!contaOrigem.getIdUsuario().equals(userId)) {
                throw new IllegalArgumentException("Usuário não autorizado");
            }

            if (contaOrigem.getSaldo().compareTo(transferenciaDto.valor()) >= 0) {
                contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(transferenciaDto.valor()));
                contaDestino.setSaldo(contaDestino.getSaldo().add(transferenciaDto.valor()));

                bancariaRepository.save(contaOrigem);
                bancariaRepository.save(contaDestino);

                String queueUrl = "http://localhost:4566/000000000000/transacao";
                String messageBody = "ID Bancaria Origem: " + contaOrigem.getIdBancaria() + "\n" +
                        "ID Usuario Origem: " + contaOrigem.getIdUsuario() + "\n" +
                        "ID Bancaria Destino: " + contaDestino.getIdBancaria() + "\n" +
                        "ID Usuario Destino: " + contaDestino.getIdUsuario() + "\n" +
                        "Tipo da Transacao: " + transferenciaDto.tipoTransacao() + "\n" +
                        "Valor: " + transferenciaDto.valor() + "\n" +
                        "data Transacao: "
                        + (LocalDate.now());

                sqsTemplate.send(queueUrl, messageBody);

                enviarAtualizacaoSaldoParaCartao(contaOrigem.getIdBancaria(), contaOrigem.getSaldo());
                enviarAtualizacaoSaldoParaCartao(contaDestino.getIdBancaria(), contaDestino.getSaldo());

                return new TransacaoResponseDto(true, "Transferência realizada com sucesso.");
            } else {
                return new TransacaoResponseDto(false,
                        "A conta de origem não possui saldo suficiente para a transferência.");
            }
        } else {
            return new TransacaoResponseDto(false, "Uma das contas bancárias não foi encontrada.");
        }
    }

    public DepositoResponseDto depositar(UUID userId, DepositoRequestDto depositoRequestDto) {
        Optional<Bancaria> optionalBancaria = bancariaRepository.findByChave(depositoRequestDto.chave());

        if (optionalBancaria.isPresent()) {
            Bancaria bancaria = optionalBancaria.get();
            if (!bancaria.getIdUsuario().equals(userId)) {
                throw new IllegalArgumentException("Usuário não autorizado");
            }

            bancaria.setSaldo(bancaria.getSaldo().add(depositoRequestDto.valor()));
            bancariaRepository.save(bancaria);

            enviarAtualizacaoSaldoParaCartao(bancaria.getIdBancaria(), bancaria.getSaldo());

            return new DepositoResponseDto(bancaria.getSaldo());
        } else {
            throw new IllegalArgumentException("Conta bancária não encontrada para a chave fornecida.");
        }
    }

    public BancariaResponseDto criarBancariaService(BancariaRequestDto bancariaDto) {
        // Crie uma nova instância de Bancaria com base nos dados do DTO
        Bancaria bancaria = new Bancaria(
                UUID.randomUUID(),
                bancariaDto.idUsuario(),
                null, // chave vazia porque o usuario vai adicionar depois
                bancariaDto.tipoConta(),
                BigDecimal.ZERO, // Defina o saldo inicial como zero
                bancariaDto.dataConta());

        // Salve a nova conta bancária no banco de dados
        Bancaria contaBancariaSalva = bancariaRepository.save(bancaria);

        // Construir a mensagem para a fila SQS
        String queueUrl = "http://localhost:4566/000000000000/cartao";
        String messageBody = "ID da Conta Bancária: " + contaBancariaSalva.getIdBancaria() + "\n" +
                "ID do Usuário: " + contaBancariaSalva.getIdUsuario() + "\n" +
                "Saldo: " + contaBancariaSalva.getSaldo();

        // Envie a mensagem para a fila SQS
        sqsTemplate.send(queueUrl, messageBody);

        // Retorne um DTO de resposta com os dados da conta bancária criada
        return new BancariaResponseDto(
                contaBancariaSalva.getTipoConta(),
                contaBancariaSalva.getSaldo());
    }

    public ChaveDto adicionarChave(UUID id, UUID userId, ChaveDto chaveDto) {
        Optional<Bancaria> optionalBancaria = bancariaRepository.findById(id);

        if (optionalBancaria.isPresent()) {
            Bancaria bancaria = optionalBancaria.get();
            if (!bancaria.getIdUsuario().equals(userId)) {
                throw new IllegalArgumentException("Usuário não autorizado");
            }

            bancaria.setChave(chaveDto.chave());
            bancariaRepository.save(bancaria);
            return new ChaveDto(chaveDto.chave());
        }
        return null;
    }

    public void deletarBancariaService(UUID idUsuario) {
        // Buscar a conta bancária pelo ID do usuário
        Optional<Bancaria> optionalBancaria = bancariaRepository.findByIdUsuario(idUsuario);

        // Verificar se a conta bancária foi encontrada
        if (optionalBancaria.isPresent()) {
            // Se encontrada, deletar a conta bancária
            bancariaRepository.delete(optionalBancaria.get());
            System.out.println("Conta bancária deletada com sucesso.");
        } else {
            // Se não encontrada, imprimir uma mensagem de erro
            System.out.println("Conta bancária não encontrada para o ID do usuário fornecido.");
        }
    }

    public void processarMensagemSQS(String mensagemSQS) {
        try {
            // Dividir a mensagem em linhas
            String[] partes = mensagemSQS.split("\n");

            // Verificar se todos os campos necessários estão presentes
            if (partes.length >= 2 && partes[0].startsWith("idUsuario") && partes[1].startsWith("acao")
                    && partes[1].contains("deletar")) {
                // Extrair o ID do usuário da mensagem
                UUID idUsuario = UUID.fromString(partes[0].split(": ")[1].trim());

                // Chamar o serviço para deletar a conta bancária
                deletarBancariaService(idUsuario);
            } else if (partes.length >= 4 && partes[0].startsWith("idUsuario") && partes[1].startsWith("tipoConta")
                    && partes[2].startsWith("dataConta") && partes[3].startsWith("acao")) {
                // Extrair o ID do usuário da mensagem
                UUID idUsuario = UUID.fromString(partes[0].split(": ")[1].trim());

                // Extrair o tipoConta da mensagem
                String tipoContaStr = partes[1].split(": ")[1].trim();
                TipoConta tipoConta = TipoConta.valueOf(tipoContaStr);

                // Extrair a dataConta da mensagem
                String dataContaStr = partes[2].split(": ")[1].trim();
                LocalDate dataConta = LocalDate.parse(dataContaStr);

                // Extrair a ação da mensagem
                String acao = partes[3].split(": ")[1].trim();

                // Verificar a ação e chamar o serviço apropriado
                if (acao.equals("criar")) {
                    // Criar um objeto BancariaRequestDto com base no idUsuario
                    BancariaRequestDto bancariaDto = new BancariaRequestDto(null, idUsuario, tipoConta, BigDecimal.ZERO,
                            dataConta);
                    // Chamar o serviço para criar a conta bancária
                    criarBancariaService(bancariaDto);
                } else {
                    // Ação desconhecida
                    System.out.println("Ação desconhecida na mensagem SQS.");
                }
            } else {
                // Mensagem incompleta
                System.out.println("Mensagem incompleta ou inválida.");
            }
        } catch (Exception e) {
            // Tratar exceções
            e.printStackTrace();
        }
    }

    public void enviarAtualizacaoSaldoParaCartao(UUID idBancaria, BigDecimal novoSaldo) {
        Optional<Bancaria> optionalBancaria = bancariaRepository.findById(idBancaria);

        if (optionalBancaria.isPresent()) {
            String queueUrl = "http://localhost:4566/000000000000/atualizasaldo";
            String messageBody = "ID da Conta Bancária: " + idBancaria + "\n" + "Valor: " + novoSaldo;
            sqsTemplate.send(queueUrl, messageBody);
        } else {
            throw new IllegalStateException("Usuário não encontrado para a conta bancária fornecida.");
        }
    }
    
    public List<ContaBancariaResponse> bancariasdoUsuario(UUID idUsuario)  {
        List<Bancaria> contas = bancariaRepository.findByIdUsuarioOrderByDataConta(idUsuario);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        if (!userId.equals(idUsuario)) {
            throw new IllegalArgumentException("Usuário não autorizado");
        }

        return contas.stream()
                .map(conta -> new ContaBancariaResponse(conta.getTipoConta(), conta.getSaldo()))
                .collect(Collectors.toList());
    }

    public BancariaResponseDto buscarContaBancariaPorIdService(UUID idBancaria) {
        Optional<Bancaria> optionalBancaria = bancariaRepository.findById(idBancaria);

        if (optionalBancaria.isPresent()) {
            Bancaria bancaria = optionalBancaria.get();

            UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
            if (!bancaria.getIdUsuario().equals(userId)) {
                throw new SecurityException("Usuário não autorizado a acessar esta conta bancária.");
            }

            return new BancariaResponseDto(bancaria.getTipoConta(), bancaria.getSaldo());
        } else {
            throw new IllegalArgumentException("Conta bancária não encontrada para o ID fornecido.");
        }
    }
}