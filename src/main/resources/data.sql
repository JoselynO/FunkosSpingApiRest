
-- Datos de ejemplo CATEGORIAS
INSERT INTO CATEGORIAS (nombre)
VALUES ('SERIE'),
       ('DISNEY'),
       ('SUPERHEROES'),
       ('PELICULAS'),
       ('OTROS');

-- Datos de ejemplo FUNKOS
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Rapunzel 1', 19.99, 50, 'https://via.placeholder.com/150', 2);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Spiderman', 14.99, 75, 'https://via.placeholder.com/150', 1);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Hulk', 16.99, 32, 'https://via.placeholder.com/150', 3);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Flash', 11.99, 29, 'https://via.placeholder.com/150', 4);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Peppa', 13.99, 12, 'https://via.placeholder.com/150', 5);
INSERT INTO FUNKOS (nombre , precio, cantidad, imagen, categoria_id) VALUES ('Petter Pan', 25.99, 14, 'https://via.placeholder.com/150', 2);

-- Contraseña: Admin1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Admin', 'Admin Admin', 'admin', 'admin@prueba.net',
        '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2');

insert into USER_ROLES (user_id, roles)
values (1, 'USER');
insert into USER_ROLES (user_id, roles)
values (1, 'ADMIN');

-- Contraseña: User1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('User', 'User User', 'user', 'user@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.');
insert into USER_ROLES (user_id, roles)
values (2, 'USER');

