package entrega.cartao.pedircartao.model;

import java.util.UUID;

import entrega.cartao.pedircartao.enums.EstadoBrasil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "entregacartao")
@AllArgsConstructor
@NoArgsConstructor
public class EntregaCartao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, name = "id_entrega")
    private UUID idEntrega;

    @Column(unique = true, name = "id_cartao")
    private UUID idCartao;

    @Column(unique = true, name = "id_usuario")
    private UUID idUsuario;
    @Column(name = "rua")
    private String rua;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoBrasil estado;

    @Column(name = "cep")
    private String cep;

    @Column(name = "numero")
    private String numero;
    
    @Column(name = "complemento")
    private String complemento;
}
