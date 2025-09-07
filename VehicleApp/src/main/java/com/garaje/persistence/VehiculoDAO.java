/**
 * DAO (Data Access Object) para la gestión de vehículos en la base de datos.
 *
 * Encapsula toda la lógica de acceso a datos de la entidad {@link Vehiculo} 
 * y expone operaciones CRUD: listar, buscar, insertar, actualizar y eliminar.
 *
 * Convenciones:
 * 
 *   Se utiliza una conexión JDBC inyectada desde el {@code VehiculoFacade}.
 *   Todas las operaciones manejan {@link SQLException}, que se registran en consola 
 *       antes de ser relanzadas a la capa superior.
 *   No se aplican reglas de negocio aquí. El DAO únicamente ejecuta SQL 
 *       contra la tabla vehiculos.
 * 
 */
package com.garaje.persistence;

import com.garaje.model.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    /** Conexión activa a la base de datos (inyectada por el facade). */
    private final Connection con;

    /**
     * Constructor que inicializa el DAO con una conexión ya creada.
     *
     * @param con conexión activa a MySQL
     */
    public VehiculoDAO(Connection con) {
        this.con = con;
    }

    /**
     * Recupera todos los vehículos almacenados en la base de datos.
     *
     * @return lista de objetos {@link Vehiculo}; vacía si no hay registros
     * @throws SQLException si ocurre un error de conexión o de consulta
     */
    public List<Vehiculo> listar() throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vehiculo v = new Vehiculo(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("color"),
                        rs.getString("propietario")
                );
                lista.add(v);
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar vehículos: " + ex.getMessage());
            throw ex;
        }
        return lista;
    }

    /**
     * Busca un vehículo por su identificador único.
     *
     * @param id identificador del vehículo
     * @return objeto {@link Vehiculo} si existe; {@code null} si no se encuentra
     * @throws SQLException si ocurre un error de conexión o consulta
     */
    public Vehiculo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM vehiculos WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Vehiculo(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("color"),
                        rs.getString("propietario")
                );
            }
        } catch (SQLException ex) {
            System.err.println("Error al buscar vehículo por id: " + ex.getMessage());
            throw ex;
        }
        return null;
    }

    /**
     * Verifica si una placa ya está registrada en la base de datos.
     *
     * @param placa placa a consultar
     * @return {@code true} si ya existe; {@code false} si no
     * @throws SQLException si ocurre un error de conexión o consulta
     */
    public boolean existePlaca(String placa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehiculos WHERE placa=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Error al verificar placa: " + ex.getMessage());
            throw ex;
        }
        return false;
    }

    /**
     * Inserta un nuevo vehículo en la base de datos.
     *
     * La validación de placa duplicada se realiza en la capa {@link VehiculoFacade}.
     *
     * @param v objeto {@link Vehiculo} a insertar
     * @throws SQLException si ocurre un error al ejecutar el INSERT
     */
    public void agregar(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, color, propietario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al agregar vehículo: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Actualiza todos los datos de un vehículo existente.
     *
     * @param v objeto {@link Vehiculo} con los nuevos valores
     * @throws SQLException si ocurre un error al ejecutar el UPDATE
     */
    public void actualizar(Vehiculo v) throws SQLException {
        String sql = "UPDATE vehiculos SET placa=?, marca=?, modelo=?, color=?, propietario=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al actualizar vehículo: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Elimina un vehículo por su identificador.
     *
     * @param id identificador del vehículo a eliminar
     * @throws SQLException si ocurre un error al ejecutar el DELETE
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM vehiculos WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al eliminar vehículo: " + ex.getMessage());
            throw ex;
        }
    }
}
