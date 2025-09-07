/**
 * Fachada que expone los métodos básicos con validaciones de negocio.
 * Valida reglas antes de llamar al DAO.
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

    @Resource(lookup = "jdbc/myPool")
    private DataSource ds;

    /**
     * Lista todos los vehículos.
     *
     * @return lista de vehículos
     * @throws SQLException si hay error en la BD
     */
    public List<Vehiculo> listar() throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.listar();
        }
    }

    /**
     * Busca vehículo por id.
     *
     * @param id identificador del vehículo
     * @return vehículo o null si no existe
     * @throws SQLException si hay error en la BD
     */
    public Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.buscarPorId(id);
        }
    }

    /**
     * Agrega un vehículo con validaciones de negocio.
     *
     * @param v vehículo a agregar
     * @throws SQLException       error en la BD
     * @throws BusinessException  si viola reglas de negocio
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
     * Actualiza un vehículo con validaciones.
     *
     * @param v vehículo a actualizar
     * @throws SQLException       error en la BD
     * @throws BusinessException  si viola reglas de negocio
     */
    public void actualizar(Vehiculo v) throws SQLException, BusinessException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Verificar existencia
            if (dao.buscarPorId(v.getId()) == null) {
                throw new BusinessException("No existe un vehículo con el id especificado.");
            }

            // Reutilizamos reglas de agregar (salvo la de placa duplicada porque es el mismo id)
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
     * Elimina un vehículo si cumple reglas de negocio.
     *
     * @param id identificador del vehículo
     * @throws SQLException       error en la BD
     * @throws BusinessException  si viola reglas de negocio
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
