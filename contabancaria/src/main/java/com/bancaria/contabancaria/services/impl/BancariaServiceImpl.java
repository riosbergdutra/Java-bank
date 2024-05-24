package com.bancaria.contabancaria.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancaria.contabancaria.dtos.bancaria.request.BancariaRequestDto;
import com.bancaria.contabancaria.dtos.bancaria.response.BancariaResponseDto;
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
public class BancariaServiceImpl implements BancariaService {

    @Autowired
    private BancariaRepository bancariaRepository;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Override
public TransacaoResponseDto realizarTransacao(TransacaoRequestDto transferenciaDto) {
    // Buscar as contas bancárias de origem e destino pelo valor da chave
    Optional<Bancaria> optionalContaOrigem = bancariaRepository.findByChave(transferenciaDto.ChaveOrigem());
    Optional<Bancaria> optionalContaDestino = bancariaRepository.findByChave(transferenciaDto.ChaveDestino());

    // Verificar se as contas foram encontradas
    if (optionalContaOrigem.isPresent() && optionalContaDestino.isPresent()) {
        Bancaria contaOrigem = optionalContaOrigem.get();
        Bancaria contaDestino = optionalContaDestino.get();

        // Verificar se a conta de origem tem saldo suficiente para a transferência
        if (contaOrigem.getSaldo().compareTo(transferenciaDto.valor()) >= 0) {
            // Subtrair o valor da conta de origem e adicioná-lo à conta de destino
            contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(transferenciaDto.valor()));
            contaDestino.setSaldo(contaDestino.getSaldo().add(transferenciaDto.valor()));

            // Salvar as atualizações no banco de dados
            bancariaRepository.save(contaOrigem);
            bancariaRepository.save(contaDestino);

            // Construir a mensagem para a fila SQS
            // Construir a mensagem para a fila SQS
            String queueUrl = "http://localhost:4566/000000000000/transacao";
            String messageBody = "Chave Origem: " + transferenciaDto.ChaveOrigem() + "\n" +
                    "ID Bancaria Origem: " + contaOrigem.getIdBancaria() + "\n" +
                    "Chave Destino: " + transferenciaDto.ChaveDestino() + "\n" +
                    "ID Usuario Origem: " + contaOrigem.getIdUsuario() + "\n" +
                    "ID Bancaria Destino: " + contaDestino.getIdBancaria() + "\n" +
                    "ID Usuario Destino: " + contaDestino.getIdUsuario() + "\n" +
                    "Tipo da Transacao: "
                    + transferenciaDto.tipoTransacao()
                    + "\n" +
                    "Valor: " + transferenciaDto.valor() + "\n" +
                    "data Transacao: "
                    + (transferenciaDto.dataTransacao() != null ? transferenciaDto.dataTransacao()
                            : LocalDate.now());

            // Envie a mensagem para a fila SQS
            sqsTemplate.send(queueUrl, messageBody);

            // retorno quando funcionar
            return new TransacaoResponseDto(true, "Transferência realizada com sucesso.");
            //retornos quando der erro
        } else {
            return new TransacaoResponseDto(false,
                    "A conta de origem não possui saldo suficiente para a transferência.");
        }
    } else {
        return new TransacaoResponseDto(false, "Uma das contas bancárias não foi encontrada.");
    }
}


    @Override
    public DepositoResponseDto depositar(DepositoRequestDto depositoRequestDto) {

        Optional<Bancaria> optionalBancaria = bancariaRepository.findByChave(depositoRequestDto.chave());

        if (optionalBancaria.isPresent()) {
            Bancaria bancaria = optionalBancaria.get();

            // Adicionar o valor do depósito ao saldo da conta bancária
            bancaria.setSaldo(bancaria.getSaldo().add(depositoRequestDto.valor()));

            // Salvar a atualização no banco de dados
            bancariaRepository.save(bancaria);

            return new DepositoResponseDto(bancaria.getSaldo());
        } else {
            throw new IllegalArgumentException("Conta bancária não encontrada para a chave fornecida.");
        }
    }

    @Override
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

        // Retorne um DTO de resposta com os dados da conta bancária criada
        return new BancariaResponseDto(
                contaBancariaSalva.getTipoConta(),
                contaBancariaSalva.getSaldo());
    }

    @Override
    public ChaveDto adicionarChave(UUID id, ChaveDto chaveDto) {
        // Buscar a conta bancária pelo ID
        Optional<Bancaria> optionalBancaria = bancariaRepository.findById(id);

        // Verificar se a conta bancária foi encontrada
        if (optionalBancaria.isPresent()) {
            Bancaria bancaria = optionalBancaria.get();

            // Adicionar a chave à conta bancária
            bancaria.setChave(chaveDto.chave());

            // Salvar a atualização no banco de dados
            bancariaRepository.save(bancaria);

            return new ChaveDto(chaveDto.chave());
        } else {
            return null;
        }
    }

    @Override
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

    @Override
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
}
