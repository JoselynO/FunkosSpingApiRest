package com.example.pedidos.models;

import com.example.funkos.models.Funko;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@TypeAlias("Pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("pedidos")
public class Pedido {

    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();
    @NotNull(message = "El id del cliente no puede ser nulo")
    private Cliente cliente;
    @NotNull(message = "El pedido debe tener al menos una linea de pedido")
    private List<LineaPedido> lineasPedido;
    @Builder.Default()
    private Integer totalItems = 0;
    @Builder.Default()
    private Double total = 0.0;
    @Builder.Default()
    private LocalDateTime createAt = LocalDateTime.now();
    @Builder.Default()
    private LocalDateTime updateAt = LocalDateTime.now();
    @Builder.Default()
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id(){
        return id.toHexString();
    }

    public void setLineasPedido(List<LineaPedido> lineasPedido) {
        this.lineasPedido = lineasPedido;
        this.totalItems = lineasPedido != null ? lineasPedido.size() : 0;
        this.total = lineasPedido != null ? lineasPedido.stream().mapToDouble(LineaPedido::getTotal).sum() : 0.0;
    }
}
