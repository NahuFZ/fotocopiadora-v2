<%-- Importamos las clases y utilidades necesarias --%>
<%@ page import="java.util.List" %>
<%@ page import="clases.Trabajo" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="utils.Utils" %>

<%-- 
  BLOQUE DE SEGURIDAD OBLIGATORIO
  Verifica si el usuario está logueado y si es un 'cliente'.
--%>
<%
	// 1. Comprueba sesión abierta de cliente
	if (!Utils.esCliente(request, response)) {
		return;
	}
    // 2. Obtener la sesión actual
    HttpSession sesion = request.getSession(false);
    String nombreRol = (String) sesion.getAttribute("nombreRol");
    
    // --- Si llegamos aquí, es un cliente válido ---
    
    // 3. Obtener la lista de trabajos (que el HistorialPedidosServlet nos envió)
    List<Trabajo> listaTrabajos = (List<Trabajo>) request.getAttribute("listaTrabajos");
    
    // 4. Obtener los filtros actuales (para que el <select> los recuerde)
    String filtroEstadoActual = (String) request.getAttribute("filtroEstadoActual");
    String ordenActual = (String) request.getAttribute("ordenActual");
    
    // 5. Preparar el formateador de fechas
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historial de Pedidos</title>
    
    <%-- Estilo simple para que la tabla no se vea tan mal --%>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>

    <a href="paginaPrincipalCliente.jsp">&lt;&lt; Volver al Panel Principal</a>

    <h1>Mi Historial de Pedidos</h1>
    
    <%-- 
      BLOQUE DE FILTROS
      Este formulario "recarga" la página (enviando a HistorialPedidosServlet GET)
      con los nuevos parámetros de filtro.
    --%>
    <form action="HistorialPedidosServlet" method="GET">
        <label for="filtroEstado">Filtrar por estado:</label>
        <select id="filtroEstado" name="filtroEstado">
            <option value="todos" <%-- Se usa 'selected' para recordar el filtro --%>
                <%= "todos".equals(filtroEstadoActual) ? "selected" : "" %>>Todos</option>
            <option value="pendiente" 
                <%= "pendiente".equals(filtroEstadoActual) ? "selected" : "" %>>Pendiente</option>
            <option value="terminado" 
                <%= "terminado".equals(filtroEstadoActual) ? "selected" : "" %>>Terminado</option>
            <option value="retirado" 
                <%= "retirado".equals(filtroEstadoActual) ? "selected" : "" %>>Retirado</option>
        </select>
        
        <label for="orden">Ordenar por:</label>
        <select id="orden" name="orden">
            <option value="fecha_sol_desc" 
                <%= "fecha_sol_desc".equals(ordenActual) ? "selected" : "" %>>Solicitud (Más nuevo)</option>
            <option value="fecha_sol_asc" 
                <%= "fecha_sol_asc".equals(ordenActual) ? "selected" : "" %>>Solicitud (Más antiguo)</option>
            <option value="fecha_retiro_desc" 
                <%= "fecha_retiro_desc".equals(ordenActual) ? "selected" : "" %>>Retiro (Más nuevo)</option>
            <option value="fecha_retiro_asc" 
                <%= "fecha_retiro_asc".equals(ordenActual) ? "selected" : "" %>>Retiro (Más antiguo)</option>
        </select>
        
        <button type="submit">Aplicar Filtros</button>
    </form>
    
    <hr>
    
    <%-- 
      BLOQUE DE TABLA DE DATOS
    --%>
    
    <%
        // Mensaje de error (si el Servlet tuvo problemas)
        Object errorHistorial = request.getAttribute("errorHistorial");
        if (errorHistorial != null) {
            out.print("<p style='color:red;'>" + errorHistorial.toString() + "</p>");
        }
        
        // Mensaje si no hay trabajos
        if (listaTrabajos == null || listaTrabajos.isEmpty()) {
            out.print("<p>Aún no has realizado ningún pedido.</p>");
        } else {
            // Si hay trabajos, dibujamos la tabla
    %>
    
    <table>
        <thead>
            <tr>
                <th>Estado</th>
                <th>Archivo</th>
                <th>Copias</th>
                <th>Calidad</th>
                <th>Faz</th>
                <th>Fecha Solicitud</th>
                <th>Fecha Retiro</th>
                <th>Fecha Impresión</th>
                <th>Fecha Entrega</th>
                <th>Acción</th>
            </tr>
        </thead>
        <tbody>
            <%-- Iteramos sobre la lista de trabajos --%>
            <% 
                for (Trabajo trabajo : listaTrabajos) { 
            %>
            <tr>
                <td><%= trabajo.getEstado() %></td>
                <%-- Columna de Archivo con enlace de previsualización --%>
                <td>
                    <!-- 
                         El enlace apunta al VerArchivoServlet con el ID del trabajo.
                         target="_blank" abre una nueva pestaña.
                    -->
                    <a href="VerArchivoServlet?id=<%= trabajo.getIdTrabajo() %>" target="_blank" style="text-decoration: none; color: blue;">
                        📄 <%= trabajo.getNombreArchivoOriginal() %>
                    </a>
                </td>
                <%-- ============================================= --%>
                
                <td><%= trabajo.getNumCopias() %></td>
                <%-- Se agrega este bloque para que blanco y negro no se vea con guiones bajos --%>
                <td>
                    <%
                        String calidad = trabajo.getCalidad();
                        if ("blanco_y_negro".equals(calidad)) {
                            out.print("Blanco y Negro");
                        } else if ("color".equals(calidad)) {
                            out.print("Color"); // Lo capitalizamos también
                        } else {
                            out.print(calidad); // Por si acaso
                        }
                    %>
                </td>
                <td><%= trabajo.getFaz() %></td>
                
                <%-- Formateamos las fechas para que sean legibles --%>
                <td><%= sdf.format(trabajo.getFechaSolicitud()) %></td>
                <td><%= sdfFecha.format(trabajo.getFechaRetiroSolicitada()) %></td>
                
                <%-- Manejamos fechas que pueden ser NULAS --%>
                <td><%= (trabajo.getFechaImpresion() != null) ? sdf.format(trabajo.getFechaImpresion()) : "N/A" %></td>
                <td><%= (trabajo.getFechaEntrega() != null) ? sdf.format(trabajo.getFechaEntrega()) : "N/A" %></td>
                
                <td>
                    <%-- 
                      BOTÓN DE BORRAR
                      Solo aparece si el estado es "pendiente".
                    --%>
                    <%
                        if ("pendiente".equals(trabajo.getEstado())) {
                    %>
                    <%-- Este es un mini-formulario que envía por POST al HistorialPedidosServlet --%>
                    <form action="HistorialPedidosServlet" method="POST" style="margin: 0;">
                        <input type="hidden" name="action" value="borrar">
                        <input type="hidden" name="idTrabajo" value="<%= trabajo.getIdTrabajo() %>">
                        <button type="submit">Borrar</button>
                    </form>
                    <%
                        } else {
                            out.print("N/A");
                        }
                    %>
                </td>
            </tr>
            <% 
                } // Fin del bucle for
            %>
        </tbody>
    </table>
    
    <%
        } // Fin del else (lista no está vacía)
    %>

</body>
</html>