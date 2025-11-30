<%@ page import="java.util.List" %>
<%@ page import="clases.Usuario" %>
<%@ page import="clases.Rol" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="utils.Utils" %>

<%-- BLOQUE DE SEGURIDAD --%>
<%
	// Comprueba sesión abierta de administrador
	if (!Utils.esAdmin(request, response)) {
		return;
	}

	HttpSession sesion = request.getSession(false);
    int idAdminLogueado = (Integer) sesion.getAttribute("idUsuario");
    
    // Recuperamos las listas enviadas por el Servlet
    List<Usuario> listaUsuarios = (List<Usuario>) request.getAttribute("listaUsuarios");
    List<Rol> listaRoles = (List<Rol>) request.getAttribute("listaRoles");

    // Establecemos el formato para la fecha de creación del usuario
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Cuentas</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .btn-cambiar { background-color: #4CAF50; color: white; border: none; padding: 5px 10px; cursor: pointer; }
        .btn-desactivar { background-color: #f44336; color: white; border: none; padding: 5px 10px; cursor: pointer; }
        .btn-activar { background-color: #008CBA; color: white; border: none; padding: 5px 10px; cursor: pointer; }
        .disabled { background-color: #ccc; cursor: not-allowed; }
    </style>
</head>
<body>

    <a href="paginaPrincipalAdmin.jsp">&lt;&lt; Volver al Panel Principal</a>

    <h1>Gestión de Cuentas de Usuarios</h1>
    
    <%
        // Mostrar mensajes de error
        String errorParam = request.getParameter("error");
    	// Error de auto-modificación del servlet
        if ("self_mod".equals(errorParam)) {
            out.print("<p style='color:red; font-weight:bold;'>Error: No puedes modificar tu propia cuenta.</p>");
        }
    %>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Email</th>
                <th>Estado</th>
                <th>Fecha creación</th>
                <th>Rol Actual</th>
                <th>Acciones de Rol</th>
                <th>Acciones de Estado</th>
            </tr>
        </thead>
        <tbody>
            <% 
                if (listaUsuarios != null) {
                    for (Usuario u : listaUsuarios) { 
                        boolean esElMismo = (u.getIdUsuario() == idAdminLogueado);
            %>
            <tr style="<%= !u.isEstaActivo() ? "background-color: #ffebee;" : "" %>">
                <td><%= u.getIdUsuario() %></td>
                <td><%= u.getNombreCompleto() %></td>
                <td><%= u.getEmail() %></td>
                
                <%-- Estado (Texto) --%>
                <td>
                    <% if (u.isEstaActivo()) { %>
                        <span style="color:green; font-weight:bold;">Activo</span>
                    <% } else { %>
                        <span style="color:red; font-weight:bold;">Inactivo</span>
                    <% } %>
                </td>
                <td><%= sdf.format(u.getFechaRegistro()) %></td>
                <td><%= u.getNombreRol() %></td>
                
                <%-- ACCIÓN 1: CAMBIAR ROL --%>
                <td>
                    <form action="GestionCuentasServlet" method="POST" style="margin:0;">
                        <input type="hidden" name="accion" value="cambiarRol">
                        <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                        
                        <select name="idNuevoRol" <%= esElMismo ? "disabled" : "" %>>
                            <% 
                                for (Rol rol : listaRoles) {
                                    // Pre-seleccionar el rol actual del usuario
                                    String selected = (rol.getIdRol() == u.getIdRol()) ? "selected" : "";
                            %>
                                <option value="<%= rol.getIdRol() %>" <%= selected %>>
                                    <%= rol.getNombreRol() %>
                                </option>
                            <% } %>
                        </select>
                        
                        <button type="submit" class="btn-cambiar <%= esElMismo ? "disabled" : "" %>" 
                                <%= esElMismo ? "disabled" : "" %>>
                            Cambiar
                        </button>
                    </form>
                </td>
                
                <%-- ACCIÓN 2: ACTIVAR / DESACTIVAR
                 	Si el usuario esta habilitado, se muestra el botón de desactivar y se esconde el de activar
                	Si el usuario esta deshabilitado, se muestra el botón de activar y se esconde el de desactivar --%>
                <td>
                    <form action="GestionCuentasServlet" method="POST" style="margin:0;">
                        <input type="hidden" name="accion" value="cambiarEstado">
                        <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                        
                        <% if (u.isEstaActivo()) { %>
                            <!-- Si está activo, mostramos botón para DESACTIVAR -->
                            <input type="hidden" name="nuevoEstado" value="false">
                            <button type="submit" class="btn-desactivar <%= esElMismo ? "disabled" : "" %>" 
                                    <%= esElMismo ? "disabled" : "" %>>
                                Desactivar
                            </button>
                        <% } else { %>
                            <!-- Si está inactivo, mostramos botón para ACTIVAR -->
                            <input type="hidden" name="nuevoEstado" value="true">
                            <button type="submit" class="btn-activar <%= esElMismo ? "disabled" : "" %>" 
                                    <%= esElMismo ? "disabled" : "" %>>
                                Activar
                            </button>
                        <% } %>
                    </form>
                </td>
            </tr>
            <% 
                    } // Fin for
                } else {
            %>
                <tr><td colspan="8">No se encontraron usuarios.</td></tr>
            <% } %>
        </tbody>
    </table>

</body>
</html>