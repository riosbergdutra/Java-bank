package com.bancaria.contabancaria.services.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancaria.contabancaria.dtos.req.BancariaRequestDto;
import com.bancaria.contabancaria.dtos.res.BancariaResponseDto;
import com.bancaria.contabancaria.model.Bancaria;
import com.bancaria.contabancaria.repository.BancariaRepository;
import com.bancaria.contabancaria.services.BancariaService;

@Service
public class BancariaServiceImpl implements BancariaService {

    @Autowired
    BancariaRepository bancariaRepository;

    @Override
    public BancariaResponseDto criarBancariaService(BancariaRequestDto bancariaDto) {
        // Crie uma nova instância de Bancaria com base nos dados do DTO
        Bancaria bancaria = new Bancaria(
            UUID.randomUUID(),
            bancariaDto.idUsuario(),
            bancariaDto.tipoConta(),
            BigDecimal.ZERO, // Defina o saldo inicial como zero
            bancariaDto.dataConta()
        );
    
        // Salve a nova conta bancária no banco de dados
        Bancaria contaBancariaSalva = bancariaRepository.save(bancaria);
    
        // Retorne um DTO de resposta com os dados da conta bancária criada
        return new BancariaResponseDto(
            contaBancariaSalva.getTipoConta(),
            contaBancariaSalva.getSaldo()
        );
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
        if (partes.length >= 1 && partes[0].startsWith("idUsuario")) {
            // Extrair o ID do usuário da mensagem
            UUID idUsuario = UUID.fromString(partes[0].split(": ")[1].trim());

            // Verificar se há um campo adicional indicando a ação
            if (partes.length >= 4 && partes[3].startsWith("acao")) {
                String acao = partes[3].split(": ")[1].trim();
                // Verificar a ação e chamar o serviço apropriado
                if (acao.equals("criar")) {
                    // Criar um objeto BancariaRequestDto com base no idUsuario
                    BancariaRequestDto bancariaDto = new BancariaRequestDto(null, idUsuario, null, null, null);
                    // Chamar o serviço para criar a conta bancária
                    criarBancariaService(bancariaDto);
                } else if (acao.equals("deletar")) {
                    // Chamar o serviço para deletar a conta bancária
                    deletarBancariaService(idUsuario);
                } else {
                    // Ação desconhecida
                    System.out.println("Ação desconhecida na mensagem SQS.");
                }
            } else {
                // Ação não especificada
                System.out.println("Ação não especificada na mensagem SQS.");
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