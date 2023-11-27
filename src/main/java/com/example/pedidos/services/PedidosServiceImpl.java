package com.example.pedidos.services;

import com.example.funkos.repositories.FunkosRepository;
import com.example.pedidos.exceptions.*;
import com.example.pedidos.models.LineaPedido;
import com.example.pedidos.models.Pedido;
import com.example.pedidos.repositories.PedidosRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@CacheConfig(cacheNames = {"pedidos"})
public class PedidosServiceImpl implements PedidosService {
    private final PedidosRepository pedidosRepository;
    private final FunkosRepository funkosRepository;

    public PedidosServiceImpl(PedidosRepository pedidosRepository, FunkosRepository funkosRepository) {
        this.pedidosRepository = pedidosRepository;
        this.funkosRepository = funkosRepository;
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable) {

        log.info("Obteniendo todos los pedidos paginados y ordenados con {}", pageable);
        return pedidosRepository.findAll(pageable);
    }

    @Override
    @Cacheable(key = "#idPedido")
    public Pedido findById(ObjectId idPedido) {
       log.info("Obteniendo pedido con id: " + idPedido);
       return pedidosRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toHexString()));
    }

    @Override
    public Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable) {
        log.info("Obteniendo pedidos del usuario con id: " + idUsuario);
        return pedidosRepository.findByIdUsuario(idUsuario, pageable);
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public Pedido save(Pedido pedido) {
        log.info("Guardando pedido:  {}", pedido);
        checkPedido(pedido);

        var pedidoToSave = reserveStockPedidos(pedido);

        pedidoToSave.setCreatedAt(LocalDateTime.now());
        pedidoToSave.setUpdatedAt(LocalDateTime.now());

        return pedidosRepository.save(pedidoToSave);

    }

    Pedido reserveStockPedidos(Pedido pedido) {
        log.info("Reservando stock del pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }

        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funkos = funkosRepository.findById(lineaPedido.getIdFunko()).get();
            funkos.setCantidad(funkos.getCantidad() - lineaPedido.getCantidad());
            if (funkos.getCantidad() < 0){
                throw new FunkoNotStock(lineaPedido.getIdFunko());
            }
            funkosRepository.save(funkos);
            lineaPedido.setTotal(lineaPedido.getCantidad() * lineaPedido.getPrecioFunko());
        });

        var total = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad() * lineaPedido.getPrecioFunko())
                .reduce(0.0, Double::sum);

        var totalItems = pedido.getLineasPedido().stream()
                .map(LineaPedido::getCantidad)
                .reduce(0, Integer::sum);

        pedido.setTotal(total);
        pedido.setTotalItems(totalItems);

        return pedido;
    }



    @Override
    @Transactional
    @CacheEvict(key = "#idPedido")
    public void delete(ObjectId idPedido) {
        log.info("Borrando pedido: " + idPedido);
        var pedidoToDelete = pedidosRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toHexString()));
        returnStockPedidos(pedidoToDelete);

        pedidosRepository.deleteById(idPedido);
    }

    @Override
    @Transactional
    @CachePut(key = "#idPedido")
    public Pedido update(ObjectId idPedido, Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);


        var pedidoToUpdate = this.findById(idPedido);
        returnStockPedidos(pedidoToUpdate);

        checkPedido(pedido);

        var pedidoToSave = reserveStockPedidos(pedido);
        pedidoToSave.setId(idPedido);
        pedidoToSave.setUpdatedAt(LocalDateTime.now());

        return pedidosRepository.save(pedidoToSave);
    }


    Pedido returnStockPedidos(Pedido pedido) {
        log.info("Retornando stock del pedido: {}", pedido);
        if (pedido.getLineasPedido() != null) {
            pedido.getLineasPedido().forEach(lineaPedido -> {
                var funko = funkosRepository.findById(lineaPedido.getIdFunko()).get();
                funko.setCantidad(funko.getCantidad() + lineaPedido.getCantidad());
                funkosRepository.save(funko);
            });
        }
        return pedido;
    }


    void checkPedido(Pedido pedido) {
        log.info("Comprobando pedido: {}", pedido);
        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            if (lineaPedido.getIdFunko() == null){
                throw new FunkoBadRequest();
            }
            var funko = funkosRepository.findById(lineaPedido.getIdFunko())
                    .orElseThrow(() -> new FunkoNotFound(lineaPedido.getIdFunko()));

            if (funko.getCantidad() < lineaPedido.getCantidad() && lineaPedido.getCantidad()> 0) {
                throw new FunkoNotStock(lineaPedido.getIdFunko());
            }

            if (!funko.getPrecio().equals(lineaPedido.getPrecioFunko())) {
                throw new FunkoBadPrice(lineaPedido.getIdFunko());
            }
        });
    }
}
