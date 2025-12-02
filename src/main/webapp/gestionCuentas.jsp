<%@ page import="java.util.List" %>
<%@ page import="clases.Usuario" %>
<%@ page import="clases.Rol" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="utils.Utils" %>

<%-- 
    =================================================================
    SECCIÓN JAVA: Seguridad y Datos
    =================================================================
--%>
<%
	// Comprueba sesión abierta de administrador
	if (!Utils.esAdmin(request, response)) {
		return;
	}

	HttpSession sesion = request.getSession(false);
	String nombreAdmin = (String) sesion.getAttribute("nombreCompleto");
    int idAdminLogueado = (Integer) sesion.getAttribute("idUsuario");
    
 	// Preparar mensaje de ERROR
    String mensajeError = null;
    
    Object errorObj = request.getAttribute("error");
    String errorParam = request.getParameter("error");
    
    if (errorObj != null) {
    	mensajeError = errorObj.toString();
    }
    
    // Recuperamos las listas enviadas por el Servlet
    List<Usuario> listaUsuarios = (List<Usuario>) request.getAttribute("listaUsuarios");
    List<Rol> listaRoles = (List<Rol>) request.getAttribute("listaRoles");

    // Establecemos el formato para la fecha de creación del usuario
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Cuentas - Fotocopiadora</title>
    	<link
		href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
		rel="stylesheet"
		integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB"
		crossorigin="anonymous">
	<!-- Integramos íconos de Bootstrap -->
	<link rel="stylesheet"
		href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
    
    <style>
        body { background-color: #f8f9fa; }
        .table-action-btn { font-size: 0.85rem; }
        .row-disabled { background-color: #f8f9fa; color: #aaa; }
        .bg-inactive { background-color: #fff5f5; } /* Fondo rojizo suave para inactivos */
    </style>
</head>
<body>
    <!-- NAVBAR ADMIN -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
        <div class="container">
        	<%-- Lado izquierdo: pequeño logo --%>
            <a class="navbar-brand fw-bold" href="paginaPrincipalAdmin.jsp">
            	<!-- Insertamos un icono de flecha hacia la izquierda --> 
                <i class="bi bi-arrow-left-circle me-2"></i>Volver al Panel
            </a>
            <%-- Lado derecho: Nombre del usuario y cerrar sesión --%>
			<div>
				<span class="navbar-text text-white mx-3"> <strong>Usuario: <%=nombreAdmin%></strong></span>
				<form action="LogoutServlet" method="POST" class="d-inline">
	                <button type="submit" class="btn btn-outline-light btn-sm">
	                    <i class="bi bi-box-arrow-right me-1"></i> Cerrar Sesión
	                </button>
		        </form>
	        </div>
        </div>
    </nav>
    <div class="container">
        
        <h2 class="text-dark fw-light mb-4"><i class="bi bi-people-fill me-2"></i>Gestión de Cuentas</h2>
		<!-- Mostramos el error en pantalla -->
        <% if (mensajeError != null) { %>
	        <div class="alert alert-danger text-center" role="alert">
	            <%= mensajeError %>
	        </div>
        <% }
           // ALERTA DE ERROR (Si intenta modificarse a sí mismo)
           if ("self_mod".equals(errorParam)) { %>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <strong>Acción no permitida:</strong> No puedes modificar tu propia cuenta desde este panel.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <% } %>

        <div class="card shadow-sm border-0">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">ID</th>
                                <th>Usuario</th>
                                <th>Email</th>
                                <th> Fecha registro </th>
                                <th class="text-center">Estado</th>
                                <th class="text-center">Rol Actual</th>
                                <th>Cambiar Rol</th>
                                <th class="text-end pe-4">Acciones de Cuenta</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            if (listaUsuarios != null) {
                                for (Usuario u : listaUsuarios) { 
                                    boolean esElMismo = (u.getIdUsuario() == idAdminLogueado);
                                    String colorRol = "bg-primary"; // Color por defecto (usado en cliente)
                                    
                                    // Estilo visual para el rol del usuario
                                    if ("admin".equals(u.getNombreRol())){
                                    	colorRol = "bg-dark";
                                    }
                            %>
                            <tr>
                                <!-- ID -->
                                <td class="ps-4 fw-bold text-muted">#<%= u.getIdUsuario() %></td>
                                
                                <!-- USUARIO -->
                                <td class="fw-bold text-dark">
                                    <%= u.getNombreCompleto() %>
                                    <% if (esElMismo) { %>
                                        <span class="badge bg-info text-dark ms-2">Tú</span>
                                    <% } %>
                                </td>
                                
                                <!-- EMAIL -->
                                <td class="text-muted"><%= u.getEmail() %></td>
                                
                                <!-- FECHA REGISTRO -->
                                <td class="text-muted"><%= sdf.format(u.getFechaRegistro()) %></td>
                                
                                <!-- ESTADO (Badge) -->
                                <td class="text-center">
                                    <% if (u.isEstaActivo()) { %>
                                        <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Activo</span>
                                    <% } else { %>
                                        <span class="badge bg-danger"><i class="bi bi-slash-circle me-1"></i>Bloqueado</span>
                                    <% } %>
                                </td>
                                
                                <!-- ROL ACTUAL (Texto) -->
                                <td class="text-center">
                                    <span class="badge <%= colorRol %> border">
                                        <%= u.getNombreRol().toUpperCase() %>
                                    </span>
                                </td>
                                
                                <!-- ACCIÓN 1: CAMBIAR ROL -->
                                <td>
                                    <form action="GestionCuentasServlet" method="POST" class="d-flex align-items-center">
                                        <input type="hidden" name="accion" value="cambiarRol">
                                        <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                                        
                                        <select name="idNuevoRol" class="form-select form-select-sm me-2" style="width: auto;" <%= esElMismo ? "disabled" : "" %>>
                                            <% for (Rol rol : listaRoles) { 
                                                String selected = (rol.getIdRol() == u.getIdRol()) ? "selected" : "";
                                            %>
                                                <option value="<%= rol.getIdRol() %>" <%= selected %>>
                                                    <%= rol.getNombreRol() %>
                                                </option>
                                            <% } %>
                                        </select>
                                        
                                        <button type="submit" class="btn btn-primary btn-sm" <%= esElMismo ? "disabled" : "" %> title="Guardar Rol">
                                            <i class="bi bi-save"></i>
                                        </button>
                                    </form>
                                </td>
                                
                                <!-- ACCIÓN 2: ACTIVAR / DESACTIVAR -->
                                <td class="text-end pe-4">
                                    <form action="GestionCuentasServlet" method="POST">
                                        <input type="hidden" name="accion" value="cambiarEstado">
                                        <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                                        
                                        <% if (u.isEstaActivo()) { %>
                                            <!-- Botón para BLOQUEAR -->
                                            <input type="hidden" name="nuevoEstado" value="false">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" <%= esElMismo ? "disabled" : "" %> 
                                                    title="Bloquear acceso al sistema" 
                                                    onclick="return confirm('¿Estás seguro de que quieres bloquear a este usuario?');">
                                                <i class="bi bi-lock-fill me-1"></i>Bloquear
                                            </button>
                                        <% } else { %>
                                            <!-- Botón para ACTIVAR -->
                                            <input type="hidden" name="nuevoEstado" value="true">
                                            <button type="submit" class="btn btn-outline-success btn-sm" <%= esElMismo ? "disabled" : "" %>
                                                    title="Restaurar acceso">
                                                <i class="bi bi-unlock-fill me-1"></i>Activar
                                            </button>
                                        <% } %>
                                    </form>
                                </td>
                            </tr>
                            <% 
                                } // Fin for
                            } else { 
                            %>
                                <tr><td colspan="8" class="text-center py-4">No se encontraron usuarios.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html>