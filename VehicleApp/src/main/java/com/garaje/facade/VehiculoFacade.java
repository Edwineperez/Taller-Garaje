/**
 * Fachada que expone las operaciones principales sobre vehículos.
 * 
 * Esta clase aplica las reglas de negocio antes de llamar
 * a la capa de persistencia ({@link VehiculoDAO}).
 *
 * Reglas de negocio implementadas:
 * 
 *   No permitir placas duplicadas.
 *   El propietario debe tener mínimo 5 caracteres.
 *   La marca, el modelo y la placa deben tener al menos 3 caracteres.
 *   El color debe estar restringido a la lista: Rojo, Blanco, Negro, Azul, Gris.
 *   No se permite registrar vehículos con más de 20 años de antigüedad.
 *   No aceptar valores sospechosos de SQL Injection (ej. “;” o “--”).
 *   Si la marca es Ferrari, se genera una notificación simulada en consola.
 *   No se puede eliminar un vehículo cuyo propietario sea “Administrador”.
 * 
 *
 * Manejo de errores:
 * 
 *   Lanza {@link BusinessException} si alguna regla de negocio es violada.
 *   Lanza {@link SQLException} si ocurre un error técnico en la base de datos.
 * 
 */
package com.garaje.facade;

import com.garaje.model.Vehiculo;
import com.garaje.persistence.VehiculoDAO;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

@Stateless
public class VehiculoFacade {

    /** Fuente de datos inyectada desde el contenedor de aplicaciones. */
    @Resource(lookup = "jdbc/myPool")
    private DataSource ds;

    /**
     * Lista todos los vehículos almacenados en la base de datos.
     *
     * @return lista de objetos {@link Vehiculo}, vacía si no hay registros
     * @throws SQLException si ocurre un error técnico en la base de datos
     */
    public List<Vehiculo> listar() throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.listar();
        }
    }

    /**
     * Busca un vehículo por su identificador único.
     *
     * @param id identificador del vehículo
     * @return objeto {@link Vehiculo} si existe, o {@code null} si no se encuentra
     * @throws SQLException si ocurre un error técnico en la base de datos
     */
    public Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.buscarPorId(id);
        }
    }

    /**
     * Agrega un vehículo nuevo, validando las reglas de negocio antes de persistirlo.
     *
     * Reglas aplicadas:
     * 
     *   No permitir placa duplicada.
     *   Propietario con mínimo 5 caracteres.
     *   Marca, modelo y placa con mínimo 3 caracteres.
     *   Color restringido (Rojo, Blanco, Negro, Azul, Gris).
     *   Antigüedad máxima de 20 años.
     *   Validación de caracteres sospechosos (evitar SQL Injection).
     *   Si la marca es Ferrari, imprimir notificación simulada.
     * 
     *
     * @param v objeto {@link Vehiculo} a agregar
     * @throws SQLException si ocurre un error en la BD
     * @throws BusinessException si alguna regla de negocio es violada
     */
    public void agregar(Vehiculo v) throws SQLException, BusinessException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Reglas de negocio
            if (dao.existePlaca(v.getPlaca())) {
                throw new BusinessException("La placa ya está registrada.");
            }
            if (v.getPropietario() == null || v.getPropietario().trim().length() < 5) {
                throw new BusinessException("El propietario debe tener al menos 5 caracteres.");
            }
            if (v.getMarca().length() < 3 || v.getModelo().length() < 3 || v.getPlaca().length() < 3) {
                throw new BusinessException("Marca, modelo y placa deben tener al menos 3 caracteres.");
            }
            List<String> coloresValidos = Arrays.asList("Rojo", "Blanco", "Negro", "Azul", "Gris");
            if (!coloresValidos.contains(v.getColor())) {
                throw new BusinessException("Color no permitido. Use: " + coloresValidos);
            }
            int anioActual = Year.now().getValue();
            int anioModelo = Integer.parseInt(v.getModelo());
            if (anioModelo < anioActual - 20) {
                throw new BusinessException("El vehículo tiene más de 20 años de antigüedad.");
            }
            if (v.getPlaca().contains(";") || v.getMarca().contains("--")) {
                throw new BusinessException("Entrada inválida, posible intento de SQL Injection.");
            }
            if ("Ferrari".equalsIgnoreCase(v.getMarca())) {
                System.out.println("📩 Notificación: Se agregó un Ferrari al sistema.");
            }

            // Si pasa validaciones, se agrega
            dao.agregar(v);
        }
    }

    /**
     * Actualiza un vehículo existente aplicando reglas de negocio.
     *
     * Reglas aplicadas:
     * 
     *   Debe existir previamente el vehículo en la BD.
     *   Propietario con mínimo 5 caracteres.
     *   Marca, modelo y placa con mínimo 3 caracteres.
     *   Color restringido (Rojo, Blanco, Negro, Azul, Gris).
     *   Antigüedad máxima de 20 años.
     *   Validación de caracteres sospechosos.
     * 
     *
     * @param v objeto {@link Vehiculo} con los nuevos datos
     * @throws SQLException si ocurre un error en la BD
     * @throws BusinessException si no existe el vehículo o se violan reglas
     */
    public void actualizar(Vehiculo v) throws SQLException, BusinessException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Verificar existencia
            if (dao.buscarPorId(v.getId()) == null) {
                throw new BusinessException("No existe un vehículo con el id especificado.");
            }

            // Validaciones de negocio
            if (v.getPropietario() == null || v.getPropietario().trim().length() < 5) {
                throw new BusinessException("El propietario debe tener al menos 5 caracteres.");
            }
            if (v.getMarca().length() < 3 || v.getModelo().length() < 3 || v.getPlaca().length() < 3) {
                throw new BusinessException("Marca, modelo y placa deben tener al menos 3 caracteres.");
            }
            List<String> coloresValidos = Arrays.asList("Rojo", "Blanco", "Negro", "Azul", "Gris");
            if (!coloresValidos.contains(v.getColor())) {
                throw new BusinessException("Color no permitido. Use: " + coloresValidos);
            }
            int anioActual = Year.now().getValue();
            int anioModelo = Integer.parseInt(v.getModelo());
            if (anioModelo < anioActual - 20) {
                throw new BusinessException("El vehículo tiene más de 20 años de antigüedad.");
            }
            if (v.getPlaca().contains(";") || v.getMarca().contains("--")) {
                throw new BusinessException("Entrada inválida, posible intento de SQL Injection.");
            }

            dao.actualizar(v);
        }
    }

    /**
     * Elimina un vehículo de la base de datos si cumple las reglas de negocio.
     *
     * Regla aplicada:
     * 
     *   No se permite eliminar un vehículo cuyo propietario sea “Administrador”.
     *
     *
     * @param id identificador del vehículo
     * @throws SQLException si ocurre un error en la BD
     * @throws BusinessException si se intenta eliminar un vehículo no permitido
     */
    public void eliminar(int id) throws SQLException, BusinessException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            Vehiculo v = dao.buscarPorId(id);
            if (v != null && "Administrador".equalsIgnoreCase(v.getPropietario())) {
                throw new BusinessException("No se puede eliminar un vehículo con propietario 'Administrador'.");
            }

            dao.eliminar(id);
        }
    }
}
