<%@ page import="java.util.List" %>
<%@ page import="clases.Trabajo" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%-- SEGURIDAD --%>
<%
    HttpSession sesion = request.getSession(false);
    if (sesion == null || !"admin".equals(sesion.getAttribute("nombreRol"))) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Trabajo> listaTrabajos = (List<Trabajo>) request.getAttribute("listaTrabajos");
    String filtroEstadoActual = (String) request.getAttribute("filtroEstadoActual");
    String ordenActual = (String) request.getAttribute("ordenActual");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
    SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Trabajos</title>
    <style>
        table { border-collapse: collapse; width: 100%; font-size: 0.9em; }
        th, td { border: 1px solid #ddd; padding: 6px; text-align: left; }
        th { background-color: #f2f2f2; }
        .btn-terminar { background-color: #4CAF50; color: white; border: none; padding: 5px; cursor: pointer; }
        .btn-entregar { background-color: #008CBA; color: white; border: none; padding: 5px; cursor: pointer; }
        .fila-pendiente { background-color: #fff; }
        .fila-terminado { background-color: #e8f5e9; } /* Verde muy claro */
        .fila-retirado { background-color: #eeeeee; color: #888; } /* Gris */
    </style>
</head>
<body>

    <a href="paginaPrincipalAdmin.jsp">&lt;&lt; Volver al Panel</a>
    <h1>Gestión de Trabajos</h1>

    <%-- FILTROS --%>
    <form action="GestionTrabajosServlet" method="GET" style="background: #eee; padding: 10px;">
        <label>Estado:</label>
        <select name="filtroEstado">
            <option value="todos" <%= "todos".equals(filtroEstadoActual) ? "selected" : "" %>>Todos</option>
            <option value="pendiente" <%= "pendiente".equals(filtroEstadoActual) ? "selected" : "" %>>Pendientes</option>
            <option value="terminado" <%= "terminado".equals(filtroEstadoActual) ? "selected" : "" %>>Terminados</option>
            <option value="retirado" <%= "retirado".equals(filtroEstadoActual) ? "selected" : "" %>>Retirados</option>
        </select>
        
        <label>Ordenar:</label>
        <select name="orden">
            <option value="nuevo" <%= "nuevo".equals(ordenActual) ? "selected" : "" %>>Más Nuevos</option>
            <option value="antiguo" <%= "antiguo".equals(ordenActual) ? "selected" : "" %>>Más Antiguos</option>
            <option value="retiro_urgente" <%= "retiro_urgente".equals(ordenActual) ? "selected" : "" %>>Fecha Retiro (Urgente)</option>
        </select>
        
        <button type="submit">Filtrar</button>
    </form>
    <br>

    <%-- TABLA --%>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Archivo</th>
                <th>Detalles</th>
                <th>Estado</th>
                <th>Fechas (Sol / Ret)</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <% 
            if (listaTrabajos != null) {
                for (Trabajo t : listaTrabajos) { 
                    // Clase CSS según estado
                    String claseFila = "fila-" + t.getEstado();
            %>
            <tr class="<%= claseFila %>">
                <td><%= t.getIdTrabajo() %></td>
                <td>
                    <b><%= t.getNombreCliente() %></b><br>
                    <small><%= t.getEmailCliente() %></small>
                </td>
                <td>
                    <a href="VerArchivoServlet?id=<%= t.getIdTrabajo() %>" target="_blank">
                        <%= t.getNombreArchivoOriginal() %>
                    </a>
                </td>
                <td>
                    <%= t.getNumCopias() %> copias<br>
                    <%= "color".equals(t.getCalidad()) ? "Color" : "B/N" %> - 
                    <%= "doble".equals(t.getFaz()) ? "Doble" : "Simple" %>
                </td>
                <td><%= t.getEstado().toUpperCase() %></td>
                <td>
                    Sol: <%= sdf.format(t.getFechaSolicitud()) %><br>
                    <b>Ret: <%= sdfFecha.format(t.getFechaRetiroSolicitada()) %></b>
                </td>
                
                <td>
                    <%-- LÓGICA DE BOTONES DE ESTADO --%>
                    <form action="GestionTrabajosServlet" method="POST" style="margin:0;">
                        <input type="hidden" name="accion" value="cambiarEstado">
                        <input type="hidden" name="idTrabajo" value="<%= t.getIdTrabajo() %>">
                        
                        <% if ("pendiente".equals(t.getEstado())) { %>
                            <input type="hidden" name="nuevoEstado" value="terminado">
                            <button type="submit" class="btn-terminar">Marcar Terminado</button>
                            
                        <% } else if ("terminado".equals(t.getEstado())) { %>
                            <input type="hidden" name="nuevoEstado" value="retirado">
                            <button type="submit" class="btn-entregar">Entregar (Retirar)</button>
                            
                        <% } else { %>
                            <span>Finalizado</span>
                        <% } %>
                    </form>
                </td>
            </tr>
            <% 
                }
            } else { %>
                <tr><td colspan="7">No hay trabajos.</td></tr>
            <% } %>
        </tbody>
    </table>

</body>
</html>