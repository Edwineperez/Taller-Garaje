package com.garaje.controller;

import com.garaje.facade.VehiculoFacade;
import com.garaje.facade.BusinessException;
import com.garaje.model.Vehiculo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet controlador para la gestión de vehículos.
 * 
 * Actúa como intermediario entre la capa de presentación (JSP) y la capa de negocio (VehiculoFacade).
 * Permite listar vehículos y agregar nuevos, aplicando las reglas de negocio definidas en la fachada.
 * 
 * Flujo:
 * 
 *   GET → consulta la lista de vehículos en la BD y los envía a la vista.
 *   POST → recibe datos de un formulario, crea un objeto Vehiculo y lo envía a la fachada para validación y persistencia.
 *
 * Manejo de errores:
 * 
 *   Si ocurre un {@link BusinessException}, se informa un mensaje claro al usuario en la vista.
 *   Si ocurre un {@link SQLException}, se muestra un mensaje genérico ("Error en la base de datos").
 *   Los mensajes de error se envían al JSP mediante atributos de request.
 * 
 */
@WebServlet("/Vehicles")
public class VehicleServlet extends HttpServlet {

    /** Fachada que contiene las reglas de negocio y acceso al DAO. */
    private final VehiculoFacade facade = new VehiculoFacade();

    /**
     * Maneja las solicitudes GET.
     * 
     * Obtiene la lista de vehículos desde la fachada y la pasa como atributo a la vista JSP.
     * Si ocurre un error de BD, se asigna un mensaje de error al request.
     *
     * @param request  petición HTTP con parámetros (si aplica)
     * @param response respuesta HTTP que será enviada al cliente
     * @throws ServletException si ocurre un error en la capa Servlet
     * @throws IOException si ocurre un error en la escritura de la respuesta
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Vehiculo> lista = facade.listar();
            request.setAttribute("Vehicles", lista);
        } catch (SQLException e) {
            request.setAttribute("error", "Error en la base de datos.");
            // log solo en consola, no mostrar al usuario
        }

        request.getRequestDispatcher("/Vehicle.jsp").forward(request, response);
    }

    /**
     * Maneja las solicitudes POST.
     * 
     * Crea un nuevo objeto {@link Vehiculo} a partir de los parámetros enviados
     * desde el formulario JSP. Llama a la fachada para aplicar las validaciones
     * de negocio y persistir el vehículo en la base de datos.
     * 
     * Flujo:
     * 
     *   Lee parámetros del formulario (placa, marca, modelo, color, propietario).
     *   Construye un objeto Vehiculo y lo pasa al método {@code agregar()} de la fachada.
     *   Si pasa validaciones, muestra un mensaje de éxito.
     *   Si ocurre una {@link BusinessException}, muestra un mensaje claro al usuario.
     *   Si ocurre un {@link SQLException}, muestra un mensaje genérico.
     *   Siempre vuelve a listar los vehículos actualizados y redirige a la vista JSP.
     * 
     *
     * @param request  petición HTTP con parámetros del formulario
     * @param response respuesta HTTP que será enviada al cliente
     * @throws ServletException si ocurre un error en la capa Servlet
     * @throws IOException si ocurre un error en la escritura de la respuesta
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Vehiculo v = new Vehiculo();
        v.setPlaca(request.getParameter("placa"));
        v.setMarca(request.getParameter("marca"));
        v.setModelo(request.getParameter("modelo"));
        v.setColor(request.getParameter("color"));
        v.setPropietario(request.getParameter("propietario"));

        try {
            facade.agregar(v);
            request.setAttribute("mensaje", "Vehículo agregado correctamente.");
        } catch (BusinessException be) {
            request.setAttribute("error", be.getMessage());
        } catch (SQLException se) {
            request.setAttribute("error", "Error técnico al guardar en la base de datos.");
        }

        // Después de procesar, volver a listar
        try {
            List<Vehiculo> lista = facade.listar();
            request.setAttribute("Vehicles", lista);
        } catch (SQLException e) {
            request.setAttribute("error", "Error al listar vehículos.");
        }

        request.getRequestDispatcher("/Vehicle.jsp").forward(request, response);
    }
}



