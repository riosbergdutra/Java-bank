package com.cartao.cartoes.services;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cartao.cartoes.dtos.criarcartao.req.CriarCartaoRequest;
import com.cartao.cartoes.dtos.criarcartao.res.CriarCartaoResponse;
import com.cartao.cartoes.enums.EnumCartao;
import com.cartao.cartoes.enums.StatusCartao;
import com.cartao.cartoes.model.Cartao;
import com.cartao.cartoes.repository.CartaoRepository;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

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

                // Gerar CVV aleatório
                int cvv = generateUniqueCVV();

                // Criar um CartaoRequest
                CriarCartaoRequest criarCartaoRequest = new CriarCartaoRequest(null, idBancaria, idUsuario, cvv, null,
                        null, null, null, saldo);

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

    void criarCartaoService(CriarCartaoRequest criarCartaoDto, int cvv) {
        Cartao cartao = new Cartao(
                UUID.randomUUID(), criarCartaoDto.idBancaria(), criarCartaoDto.idUsuario(), cvv, StatusCartao.BLOQUEADO,
                null, EnumCartao.BLOQUEADO, EnumCartao.BLOQUEADO, criarCartaoDto.saldoBancaria());

        cartaoRepository.save(cartao);
    }

    // Método para gerar um CVV aleatório e garantir unicidade no banco de dados
    int generateUniqueCVV() {
        Random random = new Random();
        int cvv;
        do {
            cvv = 100 + random.nextInt(900); // Gera um número aleatório entre 100 e 999
        } while (cartaoRepository.existsByCvv(cvv)); // Verifica se o CVV já existe no banco de dados
        return cvv;
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
}
