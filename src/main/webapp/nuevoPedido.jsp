<%@ page import="utils.Utils" %>
<%-- 
  BLOQUE DE SEGURIDAD OBLIGATORIO
  Verifica si el usuario está logueado y si es un 'cliente'.
--%>
<%
	// Comprueba sesión abierta de cliente
	if (!Utils.esCliente(request, response)) {
		return;
	}

    // 1. Obtener la sesión actual, sin crear una nueva si no existe
    HttpSession sesion = request.getSession(false);
    String nombreRol = (String) sesion.getAttribute("nombreRol");
%>

<%-- 
  Importamos las clases de Java necesarias para manejar las fechas
  y la lista de errores.
--%>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuevo Pedido de Impresión</title>
</head>
<body>

    <a href="paginaPrincipalCliente.jsp">&lt;&lt; Volver al Panel Principal</a>
    
    <h1>Solicitar un Nuevo Pedido</h1>
    <p>Complete el formulario para enviar su trabajo de impresión.</p>

    <%-- 
      Bloque de Scriptlet para mostrar la lista de errores
      que podría enviar el "NuevoPedidoServlet".
    --%>
    <%
        Object erroresObj = request.getAttribute("listaErrores");
        
        if (erroresObj != null && erroresObj instanceof List) {
            List<String> listaErrores = (List<String>) erroresObj;
            if (!listaErrores.isEmpty()) {
    %>
                <div style="color: red; border: 1px solid red; padding: 10px; margin-bottom: 15px;">
                    <strong>Error al enviar el pedido:</strong>
                    <ul>
    <%
                        for (String error : listaErrores) {
                            out.print("<li>" + error + "</li>");
                        }
    %>
                    </ul>
                </div>
    <%
            } // Fin del if (!listaErrores.isEmpty())
        } // Fin del if (erroresObj != null)
    %>
    
    <%-- 
      Mensaje de éxito si el servlet redirige aquí con ?exito=true
    --%>
    <%
        String exitoParam = request.getParameter("exito");
        if (exitoParam != null && exitoParam.equals("true")) {
            out.print("<p style='color: green; font-weight: bold;'>¡Pedido enviado con éxito!</p>");
        }
    %>

    <hr>
    
    <%-- 
      Este formulario debe tener method="POST" para poder enviar archivos
      y enctype="multipart/form-data" que es OBLIGATORIO para <input type="file">.
      Ese atributo le dice al servidor que se prepare para recibir un archivo, no solo texto.
    --%>
    <form action="NuevoPedidoServlet" method="POST" enctype="multipart/form-data">
        
        <div>
            <label for="archivo"><b>1. Seleccione su archivo (PDF o Imagen):</b></label><br>
            <input type="file" id="archivo" name="archivo" accept=".pdf,.jpg,.jpeg,.png" required>
        </div>
        <br>
        
        <div>
            <label for="num_copias"><b>2. Número de copias (1-99):</b></label><br>
            <input type="number" id="num_copias" name="num_copias" min="1" max="99" value="1" required>
        </div>
        <br>
        
        <div>
            <label for="calidad"><b>3. Calidad de impresión:</b></label><br>
            <select id="calidad" name="calidad">
                <option value="blanco_y_negro">Blanco y Negro</option>
                <option value="color">Color</option>
            </select>
        </div>
        <br>
        
        <div>
            <label for="faz"><b>4. Tipo de faz:</b></label><br>
            <select id="faz" name="faz">
                <option value="simple">Simple Faz</option>
                <option value="doble">Doble Faz</option>
            </select>
        </div>
        <br>
        
        <div>
            <label for="fecha_retiro"><b>5. Fecha para retirar:</b></label><br>
            <%-- 
              Este scriptlet obtiene la fecha de HOY (en Argentina)
              y la pone como la fecha MÍNIMA seleccionable en el calendario.
            --%>
            <%
                //LocalDate hoy = LocalDate.now(java.time.ZoneId.of("America/Argentina/Buenos_Aires"));
                // Vamos a usar la fecha local del servidor para simplificar
                LocalDate hoy = LocalDate.now();
                String fechaMinima = hoy.format(DateTimeFormatter.ISO_LOCAL_DATE);
            %>
            <input type="date" id="fecha_retiro" name="fecha_retiro" min="<%= fechaMinima %>" required>
        </div>
        <br>
        
        <div>
            <button type="submit">Enviar Pedido</button>
        </div>
        
    </form>

</body>
</html>