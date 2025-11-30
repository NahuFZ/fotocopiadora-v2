<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <title>Error de Archivo</title>
</head>
<body>
    <div style="text-align: center; margin-top: 50px; font-family: sans-serif;">
        <h2 style="color: red;">No se pudo abrir el archivo</h2>
        
        <%-- Mostramos el mensaje específico si existe --%>
        <%
            String mensaje = (String) request.getAttribute("error");
            if (mensaje != null) {
                out.print("<p><strong>Detalle:</strong> " + mensaje + "</p>");
            }
        %>
        
        <br>
        <!-- Un botón simple de JavaScript para cerrar la pestaña -->
        <button onclick="window.close()" style="padding: 10px 20px; cursor: pointer;">Cerrar Pestaña</button>
    </div>
</body>