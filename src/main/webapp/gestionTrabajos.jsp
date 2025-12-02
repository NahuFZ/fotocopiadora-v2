<%@ page import="java.util.List" %>
<%@ page import="clases.Trabajo" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="utils.Utils" %>

<%-- 
    =================================================================
    SECCIÓN JAVA: Seguridad y Datos
    =================================================================
--%>
<%
	// Comprueba sesión abierta por un administrador.
	if (!Utils.esAdmin(request, response)) {
		return;
	}
	// Obtenemos sesión y nombre del administrador
	HttpSession sesion = request.getSession(false);
	String nombreAdmin = (String) sesion.getAttribute("nombreCompleto");
    
	// Preparar mensaje de ERROR
    String mensajeError = null;
    
    Object errorObj = request.getAttribute("error");
    String errorParam = request.getParameter("error");
    
    if (errorObj != null) {
    	mensajeError = errorObj.toString();
    }
    
    // Recuperamos trabajo, filtro y orden del Servlet.
    List<Trabajo> listaTrabajos = (List<Trabajo>) request.getAttribute("listaTrabajos");
    String filtroEstadoActual = (String) request.getAttribute("filtroEstadoActual");
    String ordenActual = (String) request.getAttribute("ordenActual");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Trabajos - Fotocopiadora</title>
     <!-- Integramos BOOTSTRAP (CSS) -->
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
        /* Hacemos la fuente de la tabla un poco más compacta para que entre todo */
        .table-compact { font-size: 0.9rem; }
    </style>
</head>
<body>
    <!-- NAVBAR ADMIN (Oscura) -->
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
        
        <h2 class="text-dark fw-light mb-4"><i class="bi bi-layers-fill me-2"></i>Gestión de Trabajos</h2>
		<!-- Mostramos el error en pantalla -->
	            <% if (mensajeError != null) { %>
                    <div class="alert alert-danger text-center" role="alert">
                        <%= mensajeError %>
                    </div>
                <% } %>
        <!-- FILTROS -->
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-body bg-white">
                <form action="GestionTrabajosServlet" method="GET" class="row g-3 align-items-end">
                    
                    <div class="col-md-4">
                        <label class="form-label fw-bold small text-muted">Estado</label>
                        <select class="form-select" name="filtroEstado">
                            <option value="todos" <%= "todos".equals(filtroEstadoActual) ? "selected" : "" %>>Todos</option>
                            <option value="pendiente" <%= "pendiente".equals(filtroEstadoActual) ? "selected" : "" %>>Pendientes</option>
                            <option value="terminado" <%= "terminado".equals(filtroEstadoActual) ? "selected" : "" %>>Terminados</option>
                            <option value="retirado" <%= "retirado".equals(filtroEstadoActual) ? "selected" : "" %>>Retirados</option>
                        </select>
                    </div>
                    
                    <div class="col-md-4">
                        <label class="form-label fw-bold small text-muted">Ordenar por</label>
                        <select class="form-select" name="orden">
                            <option value="fecha_sol_desc" <%= "fecha_sol_desc".equals(ordenActual) ? "selected" : "" %>>Solicitud (Más nuevo)</option>
	                        <option value="fecha_sol_asc" <%= "fecha_sol_asc".equals(ordenActual) ? "selected" : "" %>>Solicitud (Más antiguo)</option>
	                        <option value="fecha_retiro_asc" <%= "fecha_retiro_asc".equals(ordenActual) ? "selected" : "" %>>Retiro (Más reciente)</option>
	                        <option value="fecha_retiro_desc" <%= "fecha_retiro_desc".equals(ordenActual) ? "selected" : "" %>>Retiro (Más lejano)</option>                       
	                    </select>
                    </div>
                    
                    <div class="col-md-4">
                        <button type="submit" class="btn btn-outline-primary w-100">
                            <i class="bi bi-funnel-fill me-1"></i>Aplicar Filtros
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- TABLA -->
        <div class="card shadow-sm border-0">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 table-compact">
                        <thead class="table-light">
                            <tr>
                                <th class="text-center">ID</th>
                                <th>Cliente</th>
                                <th>Archivo</th>
                                <th>Detalles</th>
                                <th class="text-center">Estado</th>
                                <th>Fechas</th>
                                <th class="text-end pe-4">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            if (listaTrabajos != null && !listaTrabajos.isEmpty()) {
                                for (Trabajo t : listaTrabajos) { 
                                    
                                    // Lógica de visualización de Estado
                                    String badgeClass = "bg-secondary";
                                    if ("pendiente".equals(t.getEstado())) badgeClass = "bg-warning text-dark";
                                    else if ("terminado".equals(t.getEstado())) badgeClass = "bg-success";
                            %>
                            <tr>
                                <!-- ID -->
                                <td class="text-center fw-bold text-muted">#<%= t.getIdTrabajo() %></td>
                                
                                <!-- CLIENTE -->
                                <td>
                                    <div class="fw-bold text-dark"><%= t.getNombreCliente() %></div>
                                    <div class="small text-muted"><i class="bi bi-envelope me-1"></i><%= t.getEmailCliente() %></div>
                                </td>
                                
                                <!-- ARCHIVO -->
                                <td>
                                    <a href="VerArchivoServlet?id=<%= t.getIdTrabajo() %>" target="_blank" class="text-decoration-none fw-bold text-primary">
                                        <i class="bi bi-file-earmark-pdf-fill me-1"></i><%= t.getNombreArchivoOriginal() %>
                                    </a>
                                </td>
                                
                                <!-- DETALLES -->
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        <%= t.getNumCopias() %> copias
                                    </span>
                                    <span class="small text-muted ms-1">
                                        <%= "color".equals(t.getCalidad()) ? "Color" : "B/N" %>, 
                                        <%= "doble".equals(t.getFaz()) ? "Doble" : "Simple" %>
                                    </span>
                                </td>
                                
                                <!-- ESTADO -->
                                <td class="text-center">
                                    <span class="badge rounded-pill <%= badgeClass %>">
                                        <%= t.getEstado().toUpperCase() %>
                                    </span>
                                </td>
                                
                                <!-- FECHAS -->
                                <td>
                                    <div class="small text-muted">Sol: <%= sdf.format(t.getFechaSolicitud()) %></div>
                                    <div class="small fw-bold text-danger">
                                        Ret: <%= sdf.format(t.getFechaRetiroSolicitada()) %>
                                    </div>
                                </td>
                                
                                <!-- ACCIONES (FORMULARIOS) -->
                                <td class="text-end pe-4">
                                    <form action="GestionTrabajosServlet" method="POST" class="d-inline">
                                    	<!-- Pasamos el id del trabajo y el nombre de la acción -->
                                        <input type="hidden" name="accion" value="cambiarEstado">
                                        <input type="hidden" name="idTrabajo" value="<%= t.getIdTrabajo() %>">
                                        
                                        <!-- Pasamos el filtro y orden elegidos por el usuario para que no se pierdan -->
                                        <input type="hidden" name="filtroEstadoActual" value="<%= filtroEstadoActual %>">
                                        <input type="hidden" name="ordenActual" value="<%= ordenActual %>">
                                        
                                        <% if ("pendiente".equals(t.getEstado())) { %>
                                            <!-- Botón: Marcar como Terminado -->
                                            <input type="hidden" name="nuevoEstado" value="terminado">
                                            <button type="submit" class="btn btn-success btn-sm table-action-btn" title="Trabajo impreso y listo">
                                                <i class="bi bi-check-lg me-1"></i>Terminado
                                            </button>
                                            
                                        <% } else if ("terminado".equals(t.getEstado())) { %>
                                            <!-- Botón: Marcar como Retirado -->
                                            <input type="hidden" name="nuevoEstado" value="retirado">
                                            <button type="submit" class="btn btn-primary btn-sm table-action-btn" title="Cliente retiró el pedido">
                                                <i class="bi bi-box-seam me-1"></i>Entregado
                                            </button>
                                            
                                        <% } else { %>
                                            <!-- Sin acciones -->
                                            <span class="text-muted small fst-italic">Finalizado</span>
                                        <% } %>
                                    </form>
                                </td>
                            </tr>
                            <% 
                                } // Fin for
                            } else { 
                            %>
                                <tr>
                                    <td colspan="7" class="text-center py-5 text-muted">
                                        <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                                        No hay trabajos que coincidan con los filtros.
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html>