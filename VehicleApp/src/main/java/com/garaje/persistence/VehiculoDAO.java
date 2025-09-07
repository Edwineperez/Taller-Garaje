/**
 * DAO para la gestión de vehículos en la base de datos.
 * Todas las operaciones CRUD y de consulta deben manejar excepciones
 * y documentar los errores detectados.
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

/**
 * VehiculoDAO realiza operaciones CRUD sobre la tabla vehiculos.
 */
public class VehiculoDAO {

    private final Connection con;

    /**
     * Inicializa con una conexión JDBC ya creada.
     *
     * @param con conexión activa a MySQL
     */
    public VehiculoDAO(Connection con) {
        this.con = con;
    }

    /**
     * Busca todos los vehículos en la base de datos.
     *
     * @return Lista de Vehiculo o lista vacía.
     * @throws SQLException si hay error de conexión BID.
     */
    public List<Vehiculo> listar() throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
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
            // Manejo de error: loggear el error y relanzar 
            System.err.println("Error al listar vehículos: "
                    + ex.getMessage());
            throw ex; // relanzar para manejar en capa superior 
        }
        return lista;
    }

    /**
     * Busca un vehículo por ID.
     * @param id
     * @return 
     * @throws java.sql.SQLException
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
            System.err.println("Error al buscar vehículo por id: "
                    + ex.getMessage());
            throw ex;
        }
        return null;
    }

    /**
     * Verifica si ya existe una placa registrada. Útil para reglas de negocio.
     *
     * @param placa placa a buscar
     * @return true si existe, false si no
     * @throws java.sql.SQLException
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
            System.err.println("Error al verificar placa: "
                    + ex.getMessage());
            throw ex;
        }
        return false;
    }

    /**
     * Agrega un nuevo vehículo si la placa no existe. Lanzar SQLException si
     * falla.
     * @param v
     * @throws java.sql.SQLException
     */
    public void agregar(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, color, propietario) VALUES( ?,  ?,  ?,  ?,  ?)"; 
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al agregar vehículo: "
                    + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Actualiza todos los datos de un vehículo existente por id.
     * @param v
     * @throws java.sql.SQLException
     */
    public void actualizar(Vehiculo v) throws SQLException {
        String sql = "UPDATE vehiculos SET placa=?, marca=?, modelo=?, color =  ?, propietario =  ? WHERE  id =  ?"; 
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al actualizar vehículo: "
                    + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Borra un vehículo por id.
    public
     * @param id
     * @throws java.sql.SQLException
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM vehiculos WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al eliminar vehículo: "
                    + ex.getMessage());
            throw ex;
        }
    }
}
