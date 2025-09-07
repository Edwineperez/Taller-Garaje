<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<html> 
<head> 
    <title>Lista de Vehículos</title> 
</head> 
<body> 
    <h2>Agregar Vehículo</h2> 
    <form action="vehicles" method="post"> 
        Marca: <input type="text" name="brand" /><br/> 
        Modelo: <input type="text" name="model" /><br/> 
        Año: <input type="text" name="year" /><br/> 
        Propietario: <input type="text" name="owner" /><br/> 
        <input type="submit" value="Agregar" /> 
    </form> 
<h2>Lista de Vehículos</h2> 
<table border="1"> 
<tr> 
<th>ID</th> 
<th>Marca</th> 
<th>Modelo</th> 
<th>Año</th> 
<th>Propietario</th> 
</tr> 
<c:forEach var="vehicle" items="${vehicles}"> 
<tr> 
<td>${vehicle.id}</td> 
<td>${vehicle.brand}</td> 
<td>${vehicle.model}</td> 
<td>${vehicle.year}</td> 
<td>${vehicle.owner}</td> 
</tr> 
</c:forEach> 
</table> 
</body> 
</html>