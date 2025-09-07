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

@WebServlet("/vehiculos")
public class VehicleServlet extends HttpServlet {

    private final VehiculoFacade facade = new VehiculoFacade();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Vehiculo> lista = facade.listar();
            request.setAttribute("vehiculos", lista);
        } catch (SQLException e) {
            request.setAttribute("error", "Error en la base de datos.");
            // log solo en consola, no mostrar al usuario
        }

        request.getRequestDispatcher("/vehiculos.jsp").forward(request, response);
    }

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
            request.setAttribute("vehiculos", lista);
        } catch (SQLException e) {
            request.setAttribute("error", "Error al listar vehículos.");
        }

        request.getRequestDispatcher("/vehiculos.jsp").forward(request, response);
    }
}


