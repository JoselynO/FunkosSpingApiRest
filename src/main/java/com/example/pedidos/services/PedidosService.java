package com.example.pedidos.services;

import com.example.pedidos.models.Pedido;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PedidosService {
    Page<Pedido> findAll(Pageable pageable);

    Pedido findById(ObjectId idPedido);

    Pedido save(Pedido pedido);

    void delete(ObjectId idPedido);

    Pedido update(ObjectId idPedido, Pedido pedido);
}
