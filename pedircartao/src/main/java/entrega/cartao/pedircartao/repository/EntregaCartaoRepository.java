package entrega.cartao.pedircartao.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import entrega.cartao.pedircartao.model.EntregaCartao;

public interface EntregaCartaoRepository extends JpaRepository<EntregaCartao, UUID> {

}
