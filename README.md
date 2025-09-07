# Taller-Garaje

Proyecto académico en Java EE para la gestión de vehículos, aplicando la arquitectura por capas:
- **DAO (Persistencia)**
- **Facade (Reglas de negocio)**
- **Servlet (Controlador web)**
- **JSP (Vista)**

---

## 📂 Arquitectura del Proyecto

- **com.garaje.model**  
  Entidades Java que representan la tabla `vehiculos`.  
  - `Vehiculo.java` (para BD en español).  
  - `Vehicle.java` (para el servlet en inglés).

- **com.garaje.persistence**  
  Acceso a datos con JDBC.  
  - `VehiculoDAO.java`

- **com.garaje.facade**  
  Contiene las reglas de negocio y usa el DAO.  
  - `VehiculoFacade.java`  
  - `BusinessException.java`

- **com.garaje.controller**  
  Controlador web (Servlet).  
  - `VehicleServlet.java`

- **WebContent / JSP**  
  - `Vehicles.jsp`

---

## 🔑 Reglas de Negocio

Implementadas en `VehiculoFacade`:
1. No permitir agregar vehículos con placa duplicada.
2. Propietario no vacío y mínimo 5 caracteres.
3. Marca, modelo y placa con mínimo 3 caracteres.
4. Color restringido a: Rojo, Blanco, Negro, Azul, Gris.
5. Vehículo no puede superar 20 años de antigüedad.
6. Prohibido eliminar vehículos cuyo propietario sea "Administrador".
7. Validaciones básicas contra SQL Injection (simuladas).
8. Si la marca es "Ferrari", se genera una notificación (simulada en consola).

## ⚙️ Manejo de Excepciones

- **DAO** → lanza `SQLException` (errores técnicos).
- **Facade** → valida reglas y lanza `BusinessException`.
- **Servlet** → captura y muestra:
  - `SQLException` → mensaje genérico para el usuario.
  - `BusinessException` → mensaje claro y amigable en la vista JSP.

## 🚀 Despliegue

1. Configurar base de datos MySQL:
   ```sql
   CREATE TABLE vehiculos (
     id INT AUTO_INCREMENT PRIMARY KEY,
     placa VARCHAR(20) NOT NULL,
     marca VARCHAR(30) NOT NULL,
     modelo VARCHAR(30) NOT NULL,
     color VARCHAR(20),
     propietario VARCHAR(50) NOT NULL
   );
