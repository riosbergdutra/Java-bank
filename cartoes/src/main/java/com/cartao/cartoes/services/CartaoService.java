package com.cartao.cartoes.services;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cartao.cartoes.dtos.ativarcartao.req.AtivarCartaoRequest;
import com.cartao.cartoes.dtos.criarcartao.req.CriarCartaoRequest;
import com.cartao.cartoes.dtos.criarcartao.res.CriarCartaoResponse;
import com.cartao.cartoes.dtos.pedircartao.req.PedirCartaoRequest;
import com.cartao.cartoes.dtos.pedircartao.res.PedirCartaoResponse;
import com.cartao.cartoes.enums.EnumCartao;
import com.cartao.cartoes.enums.StatusCartao;
import com.cartao.cartoes.model.Cartao;
import com.cartao.cartoes.repository.CartaoRepository;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private SqsTemplate sqsTemplate;

    // Método para processar mensagem SQS e salvar um novo cartão
    public CriarCartaoResponse processarMensagemSQSCriarCartao(String mensagemSQS) {
        try {
            // Dividir a mensagem em linhas
            String[] partes = mensagemSQS.split("\n");

            // Verificar se todos os campos necessários estão presentes
            if (partes.length >= 3 &&
                    partes[0].startsWith("ID da Conta Bancária") &&
                    partes[1].startsWith("ID do Usuário") &&
                    partes[2].startsWith("Saldo")) {

                // Extrair os dados da mensagem
                UUID idBancaria = UUID.fromString(partes[0].split(": ")[1].trim());
                UUID idUsuario = UUID.fromString(partes[1].split(": ")[1].trim());
                BigDecimal saldo = new BigDecimal(partes[2].split(": ")[1].trim());

                // Gerar número da conta aleatório e único
                String numeroConta = gerarNumeroDeCartao();

                // Gerar CVV aleatório
                String cvv = gerarCVV();

                // Criar um CartaoRequest
                CriarCartaoRequest criarCartaoRequest = new CriarCartaoRequest(null, idBancaria, idUsuario, cvv,
                        numeroConta,
                        null, null, null, null, saldo, false);

                // Salvar o novo cartão no banco de dados
                criarCartaoService(criarCartaoRequest, cvv);
                return new CriarCartaoResponse(true, "Cartão criado com sucesso.");
            } else {
                // Mensagem incompleta ou inválida
                return new CriarCartaoResponse(false, "Mensagem incompleta ou inválida.");
            }
        } catch (Exception e) {
            // Tratar exceções
            e.printStackTrace();
            return new CriarCartaoResponse(false, "Erro ao processar a mensagem: " + e.getMessage());
        }
    }

    void criarCartaoService(CriarCartaoRequest criarCartaoDto, String cvv) {
        Cartao cartao = new Cartao(
                UUID.randomUUID(), criarCartaoDto.idBancaria(), criarCartaoDto.idUsuario(), cvv,
                criarCartaoDto.numeroCartao(), StatusCartao.BLOQUEADO,
                null, EnumCartao.BLOQUEADO, EnumCartao.BLOQUEADO, criarCartaoDto.saldoBancaria(), false);

        cartaoRepository.save(cartao);
    }

    public String gerarNumeroDeCartao() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("512345");

        // Gera os próximos 10 dígitos do número do cartão
        for (int i = 0; i < 10; i++) {
            cardNumber.append(random.nextInt(10));
        }

        // Calcula o dígito de verificação usando o algoritmo de Luhn
        int luhnDigit = calculateLuhnDigit(cardNumber.toString());

        // Adiciona o dígito de verificação ao número do cartão
        cardNumber.append(luhnDigit);

        // Verifica se o número do cartão gerado já existe no banco de dados
        while (cartaoRepository.existsByNumeroCartao(cardNumber.toString())) {
            // Se o número já existe, gera um novo número de cartão
            cardNumber.replace(6, 16, ""); // Remove os 10 últimos dígitos
            for (int i = 0; i < 10; i++) {
                cardNumber.append(random.nextInt(10));
            }
            // Recalcula o dígito de verificação
            luhnDigit = calculateLuhnDigit(cardNumber.toString());
            cardNumber.append(luhnDigit);
        }

        return cardNumber.toString();
    }

    public String gerarCVV() {
        Random random = new Random();
        String cvv = String.format("%03d", random.nextInt(1000)); // Gera um CVV de 3 dígitos
        return cvv;
    }

    public int calculateLuhnDigit(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum * 9) % 10;
    }

    public CriarCartaoResponse processarMensagemSQSAtualizarSaldo(String mensagemSQS) {
        try {
            // Dividir a mensagem em linhas
            String[] partes = mensagemSQS.split("\n");

            // Verificar se todos os campos necessários estão presentes
            if (partes.length >= 2 &&
                    partes[0].startsWith("ID da Conta Bancária") &&
                    partes[1].startsWith("Valor")) {

                // Extrair os dados da mensagem
                UUID idBancaria = UUID.fromString(partes[0].split(": ")[1].trim());
                BigDecimal valor = new BigDecimal(partes[1].split(": ")[1].trim());

                // Buscar o cartão associado à conta bancária
                Optional<Cartao> optionalCartao = cartaoRepository.findByIdBancaria(idBancaria);

                if (optionalCartao.isPresent()) {
                    Cartao cartao = optionalCartao.get();

                    // Atualizar o saldo do cartão
                    cartao.setSaldoBancaria(valor);

                    // Salvar a atualização no banco de dados
                    cartaoRepository.save(cartao);

                    return new CriarCartaoResponse(true, "Saldo do cartão atualizado com sucesso.");
                } else {
                    return new CriarCartaoResponse(false, "Cartão não encontrado para a conta bancária fornecida.");
                }
            } else {
                // Mensagem incompleta ou inválida
                return new CriarCartaoResponse(false, "Mensagem incompleta ou inválida.");
            }
        } catch (Exception e) {
            // Tratar exceções
            e.printStackTrace();
            return new CriarCartaoResponse(false, "Erro ao processar a mensagem: " + e.getMessage());
        }
    }

    public PedirCartaoResponse pedirEntregaCartao(PedirCartaoRequest pedirCartaoRequest, UUID id, UUID userId) {
        Optional<Cartao> optionalCartao = cartaoRepository.findById(id);

        try {
            // Buscar o cartão correspondente ao pedido

            if (optionalCartao.isPresent()) {
                Cartao cartao = optionalCartao.get();
                if (!cartao.getIdUsuario().equals(userId)) {
                    throw new IllegalArgumentException("Usuário não autorizado");
                }
                // Verificar se o cartão já foi pedido antes
                if (cartao.getCartaoPedido()) {
                    return new PedirCartaoResponse(false, "Pedido de entrega do cartão já realizado anteriormente.");
                }

                // Transformar cartaoPedido de false para true
                cartao.setCartaoPedido(true);

                // Salvar a atualização no banco de dados
                cartaoRepository.save(cartao);

                // Construir a mensagem para a fila SQS
                String queueUrl = "http://localhost:4566/000000000000/entregacartao";
                String messageBody = "id do Cartao: " + cartao.getIdCartao() + "\n" +
                        "id do Usuario: " + cartao.getIdUsuario() + "\n" +
                        "Rua: " + pedirCartaoRequest.rua() + "\n" +
                        "Cidade: " + pedirCartaoRequest.cidade() + "\n" +
                        "Estado: " + pedirCartaoRequest.estado() + "\n" +
                        "CEP: " + pedirCartaoRequest.cep() + "\n" +
                        "Número: " + pedirCartaoRequest.numero() + "\n" +
                        "Complemento: " + pedirCartaoRequest.complemento();

                // Envie a mensagem para a fila SQS
                sqsTemplate.send(queueUrl, messageBody);

                return new PedirCartaoResponse(true, "Pedido de entrega do cartão enviado com sucesso.");
            } else {
                // Cartão não encontrado
                return new PedirCartaoResponse(false, "Cartão não encontrado para o ID fornecido.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new PedirCartaoResponse(false, "Erro ao enviar pedido de entrega do cartão: " + e.getMessage());
        }
    }


    public CriarCartaoResponse ativarCartao(UUID id, AtivarCartaoRequest request, UUID userId) {
        try {
            Optional<Cartao> optionalCartao = cartaoRepository.findById(id);
    
            if (optionalCartao.isPresent()) {
                Cartao cartao = optionalCartao.get();
                if (!cartao.getIdUsuario().equals(userId)) {
                    throw new IllegalArgumentException("Usuário não autorizado");
                }
                // Verificar se os dados fornecidos correspondem ao cartão
                if (request.numeroCartao().equals(cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4)) &&
                    request.cvv().equals(cartao.getCvv())) {
                    
                    if (cartao.getStatusCartao() == StatusCartao.BLOQUEADO) {
                        cartao.setStatusCartao(StatusCartao.ATIVO);
                        
                        // Desbloquear automaticamente o cartão de débito
                        if (cartao.getDebito() == EnumCartao.BLOQUEADO) {
                            cartao.setDebito(EnumCartao.DESBLOQUEADO);
                        }
        
                        cartaoRepository.save(cartao);
                        return new CriarCartaoResponse(true, "Cartão ativado com sucesso.");
                    } else {
                        return new CriarCartaoResponse(false, "O cartão já está ativo.");
                    }
                } else {
                    return new CriarCartaoResponse(false, "Dados do cartão inválidos.");
                }
            } else {
                return new CriarCartaoResponse(false, "Cartão não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CriarCartaoResponse(false, "Erro ao ativar cartão: " + e.getMessage());
        }
    }
    
}
