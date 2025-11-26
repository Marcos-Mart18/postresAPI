-------------------------------------------------------
-- ROLES
-------------------------------------------------------

INSERT INTO ROL (id_rol, nombre, is_active)
VALUES (SQ_ROL.nextval, 'ADMIN', 'A');

INSERT INTO ROL (id_rol, nombre, is_active)
VALUES (SQ_ROL.nextval, 'CLIENTE', 'A');

INSERT INTO ROL (id_rol, nombre, is_active)
VALUES (SQ_ROL.nextval, 'REPARTIDOR', 'A');

INSERT INTO ROL (id_rol, nombre, is_active)
VALUES (SQ_ROL.nextval, 'USER', 'A');

-------------------------------------------------------
-- PERSONA (para cliente)
-------------------------------------------------------

INSERT INTO PERSONA (id_persona, nombres, apellidos, dni, correo, telefono, direccion, is_active)
VALUES (SQ_PERSONA.nextval, 'Cliente', 'Uno', '00000002', 'cliente@example.com', '999111222', 'Calle 123', 'A');

-------------------------------------------------------
-- REPARTIDOR
-------------------------------------------------------

INSERT INTO REPARTIDOR (id_repartidor, codigo, is_active)
VALUES (SQ_REPARTIDOR.nextval, 'R001', 'A');

-------------------------------------------------------
-- USUARIO: ADMIN
-------------------------------------------------------

INSERT INTO USUARIO (id_usuario, username, contrasena, profile_foto_url, is_active)
VALUES (
           SQ_USUARIO.nextval,
           'admin',
           '$2a$12$CvijwGchp2Sd1/Hb4uYNk.DxC46nuZAmpylZbO/xuBYccTPdr4qYW',
           'https://res.cloudinary.com/demo/image/upload/v1/defaults/user.png',
           'A'
       );

-------------------------------------------------------
-- USUARIO: CLIENTE
-------------------------------------------------------

INSERT INTO USUARIO (
    id_usuario, username, contrasena, profile_foto_url, is_active, id_persona
)
VALUES (
           SQ_USUARIO.nextval,
           'cliente',
           '$2a$12$CvijwGchp2Sd1/Hb4uYNk.DxC46nuZAmpylZbO/xuBYccTPdr4qYW',
           'https://res.cloudinary.com/demo/image/upload/v1/defaults/user.png',
           'A',
           (SELECT id_persona FROM PERSONA WHERE dni='00000002')
       );

-------------------------------------------------------
-- USUARIO: REPARTIDOR
-------------------------------------------------------

INSERT INTO USUARIO (
    id_usuario, username, contrasena, profile_foto_url, is_active, id_repartidor
)
VALUES (
           SQ_USUARIO.nextval,
           'repartidor',
           '$2a$12$CvijwGchp2Sd1/Hb4uYNk.DxC46nuZAmpylZbO/xuBYccTPdr4qYW',
           'https://res.cloudinary.com/demo/image/upload/v1/defaults/user.png',
           'A',
           (SELECT id_repartidor FROM REPARTIDOR WHERE codigo='R001')
       );

-------------------------------------------------------
-- ROLES ASIGNADOS
-------------------------------------------------------

INSERT INTO USUARIO_ROL (idusuario_rol, id_usuario, id_rol)
VALUES (
           SQ_USUARIO_ROL.nextval,
           (SELECT id_usuario FROM USUARIO WHERE username='admin'),
           (SELECT id_rol FROM ROL WHERE nombre='ADMIN')
       );

INSERT INTO USUARIO_ROL (idusuario_rol, id_usuario, id_rol)
VALUES (
           SQ_USUARIO_ROL.nextval,
           (SELECT id_usuario FROM USUARIO WHERE username='cliente'),
           (SELECT id_rol FROM ROL WHERE nombre='CLIENTE')
       );

INSERT INTO USUARIO_ROL (idusuario_rol, id_usuario, id_rol)
VALUES (
           SQ_USUARIO_ROL.nextval,
           (SELECT id_usuario FROM USUARIO WHERE username='repartidor'),
           (SELECT id_rol FROM ROL WHERE nombre='REPARTIDOR')
       );

-------------------------------------------------------
-- ESTADOS DE PEDIDO
-------------------------------------------------------

INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'PENDIENTE');
INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'ACEPTADO');
INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'EN_PREPARACION');
INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'LISTO_PARA_ENTREGA');
INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'ASIGNADO');
INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'EN_CAMINO');
INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'ENTREGADO');
INSERT INTO ESTADO (id_estado, nombre) VALUES (SQ_ESTADO.nextval, 'CANCELADO');
