package com.example.rest.users.services;

import com.example.pedidos.repositories.PedidosRepository;
import com.example.rest.users.dto.UserInfoResponse;
import com.example.rest.users.dto.UserRequest;
import com.example.rest.users.dto.UserResponse;
import com.example.rest.users.exceptions.UserNameOrEmailExists;
import com.example.rest.users.exceptions.UserNotFound;
import com.example.rest.users.mappers.UsersMapper;
import com.example.rest.users.models.User;
import com.example.rest.users.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PedidosRepository pedidosRepository;
    private final UsersMapper usersMapper;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, PedidosRepository pedidosRepository, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.pedidosRepository = pedidosRepository;
        this.usersMapper = usersMapper;
    }

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todos los usuarios con username: " + username + " y borrados: " + isDeleted);
        // Criterio de búsqueda por nombre
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por email
        Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por borrado
        Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Combinamos las especificaciones
        Specification<User> criterio = Specification.where(specUsernameUser)
                .and(specEmailUser)
                .and(specIsDeleted);

        // Debe devolver un Page, por eso usamos el findAll de JPA
        return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    public UserInfoResponse findById(Long id) {
        log.info("Buscando usuario por id: " + id);
        var user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        var pedidos = pedidosRepository.findPedidosIdsByIdUsuario(id).stream().map(p -> p.getId().toHexString()).toList();
        return usersMapper.toUserInfoResponse(user, pedidos);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: " + userRequest);
        // No debe existir otro con el mismo username o email
        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario: " + userRequest);
        usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        // No debe existir otro con el mismo username o email, y si existe soy yo mismo
        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        System.out.println("usuario encontrado: " + u.getId() + " Mi id: " + id);
                        throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                    }
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest, id)));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        log.info("Borrando usuario por id: " + id);
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        //Hacemos el borrado fisico si no hay pedidos
        if (pedidosRepository.existsByIdUsuario(id)) {
            // Si no, lo marcamos como borrado lógico
            log.info("Borrado lógico de usuario por id: " + id);
            usersRepository.updateIsDeletedToTrueById(id);
        } else {
            // Si hay pedidos, lo borramos físicamente
            log.info("Borrado físico de usuario por id: " + id);
            usersRepository.delete(user);
        }
    }
}