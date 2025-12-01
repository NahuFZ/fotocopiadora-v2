<%-- Importamos las clases y utilidades necesarias --%>
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
	// Comprueba sesión abierta de cliente
// 	if (!Utils.esCliente(request, response)) {
// 		return;
// 	}
    
    // --- Si llegamos aquí, es un cliente válido ---
    
    // Preparar mensaje de ERROR
    String mensajeError = null;
    
    Object errorObj = request.getAttribute("error");
    String errorParam = request.getParameter("error");
    
    if (errorObj != null) {
    	mensajeError = errorObj.toString();
    }
    
    // Obtener la sesión actual, sin crear una nueva si no existe
    HttpSession sesion = request.getSession(false);
    // Buscamos el nombre del usuario
    String nombreCliente = (String) sesion.getAttribute("nombreCompleto");
    
    // Obtiene la lista de trabajos (que el HistorialPedidosServlet nos envió)
    List<Trabajo> listaTrabajos = (List<Trabajo>) request.getAttribute("listaTrabajos");
    
    // Obtiene los filtros actuales (para que el <select> los recuerde)
    String filtroEstadoActual = (String) request.getAttribute("filtroEstadoActual");
    String ordenActual = (String) request.getAttribute("ordenActual");
    
    // Prepara el formateador de fechas
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
    <title>Historial de Pedidos - Fotocopiadora</title>
    <!-- Integramos BOOTSTRAP (CSS) -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" 
		rel="stylesheet" integrity ="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <!-- Integramos íconos de Bootstrap -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
    
    <%-- Estilo simple para que la tabla no se vea tan mal --%>
    <style>
       	body { background-color: #f8f9fa; }
        table { 
        	border-collapse: collapse;
         	width: 100%;
         	 }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
	<!-- BARRA DE NAVEGACIÓN -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
		<div class="container">
			<a class="navbar-brand fw-bold" href="paginaPrincipalCliente.jsp">
				<!-- Insertamos un icono de flecha hacia la izquierda --> 
				<i class="bi bi-arrow-left-circle me-2"></i>Volver al Panel
			</a> <span class="navbar-text text-white"> <strong>Usuario: <%=nombreCliente%></strong>
			</span>
		</div>
	</nav>
    
    <div class="container">
    
    	<div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="text-primary fw-light"><i class="bi bi-clock-history me-2"></i>Mis Pedidos</h2>
        </div>
        <!-- TARJETA DE FILTROS -->
        <div class="card shadow-sm border-0 mb-4">
        <div class="card-body bg-white">
            <form action="HistorialPedidosServlet" method="GET" class="row g-3 align-items-end">
                
                <div class="col-md-4">
                    <label class="form-label fw-bold small text-muted">Filtrar Estado</label>
                    <select class="form-select" name="filtroEstado">
                        <option value="todos" <%= "todos".equals(filtroEstadoActual) ? "selected" : "" %>>Todos</option>
                        <option value="pendiente" <%= "pendiente".equals(filtroEstadoActual) ? "selected" : "" %>>Pendiente</option>
                        <option value="terminado" <%= "terminado".equals(filtroEstadoActual) ? "selected" : "" %>>Terminado</option>
                        <option value="retirado" <%= "retirado".equals(filtroEstadoActual) ? "selected" : "" %>>Retirado</option>
                    </select>
                </div>
                
                <div class="col-md-4">
                    <label class="form-label fw-bold small text-muted">Ordenar</label>
                    <select class="form-select" name="orden">
                        <option value="fecha_sol_desc" <%= "fecha_sol_desc".equals(ordenActual) ? "selected" : "" %>>Solicitud (Más nuevo)</option>
                        <option value="fecha_sol_asc" <%= "fecha_sol_asc".equals(ordenActual) ? "selected" : "" %>>Solicitud (Más antiguo)</option>
                        <option value="fecha_retiro_desc" <%= "fecha_retiro_desc".equals(ordenActual) ? "selected" : "" %>>Retiro (Más nuevo)</option>
                        <option value="fecha_retiro_asc" <%= "fecha_retiro_asc".equals(ordenActual) ? "selected" : "" %>>Retiro (Más antiguo)</option>
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
        <!-- TARJETA PRINCIPAL -->
        <div class="card shadow-sm border-0">
        <div class="card-body p-0">
        	<%-- MOSTRAMOS ERRORES --%>
		    <% if (mensajeError != null) { %>
			        <div class="alert alert-danger text-center" role="alert">
			            <%= mensajeError %>
			        </div>
            <%
               }
               // Verificamos que existan trabajos.
               if (listaTrabajos == null || listaTrabajos.isEmpty()) {
            %>
             	 <%-- No existen trabajos --%>
                 <div class="text-center py-5">
                     <i class="bi bi-inbox fs-1 text-muted"></i>
                     <p class="mt-3 text-muted">No se encontraron pedidos con estos filtros.</p>
                 </div>
			<%
			   } else {
			%>
			<%-- COMIENZO DE LA TABLA --%>
            <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Estado</th>
                        <th>Archivo</th>
                        <th>Detalles</th>
                        <th>Solicitud / Retiro</th>
                        <th>Impresión</th> <!-- NUEVA COLUMNA -->
                        <th>Entrega</th>   <!-- NUEVA COLUMNA -->
                        <th class="text-end pe-4">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Trabajo t : listaTrabajos) { 
                        String badgeClass = "bg-secondary";
                        
                        if ("pendiente".equals(t.getEstado())) {
                            badgeClass = "bg-warning text-dark";
                        } else if ("terminado".equals(t.getEstado())) {
                            badgeClass = "bg-success";
                        } else if ("retirado".equals(t.getEstado())) {
                            badgeClass = "bg-secondary";
                        }
                    %>
                    <tr>
                        <!-- ESTADO -->
                        <td class="text-center">
                            <span class="badge rounded-pill <%= badgeClass %>">
                                <%= t.getEstado().toUpperCase() %>
                            </span>
                        </td>
                        
                        <!-- ARCHIVO -->
                        <td>
                            <a href="VerArchivoServlet?id=<%= t.getIdTrabajo() %>" target="_blank" class="text-decoration-none fw-bold">
                                <i class="bi bi-file-earmark-text me-1"></i><%= t.getNombreArchivoOriginal() %>
                            </a>
                        </td>
                        
                        <!-- DETALLES -->
                        <td>
                            <small class="text-muted d-block">
                                <strong><%= t.getNumCopias() %></strong> copias
                            </small>
                            <small class="text-muted">
                                <%= "color".equals(t.getCalidad()) ? "Color" : "Blanco y negro" %> • 
                                <%= "doble".equals(t.getFaz()) ? "Doble" : "Simple" %>
                            </small>
                        </td>
                        
                        <!-- FECHAS BÁSICAS -->
                        <td>
                            <div class="small text-muted">Sol: <%= sdf.format(t.getFechaSolicitud()) %></div>
                            <div class="small fw-bold text-dark">
                                <i class="bi bi-calendar-event me-1"></i>Ret: <%= sdfFecha.format(t.getFechaRetiroSolicitada()) %>
                            </div>
                        </td>
                        
                        <!-- FECHA IMPRESIÓN (NUEVA) -->
                        <td>
                            <small class="text-muted">
                                <%= (t.getFechaImpresion() != null) ? sdf.format(t.getFechaImpresion()) : "-" %>
                            </small>
                        </td>
                        
                        <!-- FECHA ENTREGA (NUEVA) -->
                        <td>
                            <small class="text-muted">
                                <%= (t.getFechaEntrega() != null) ? sdf.format(t.getFechaEntrega()) : "-" %>
                            </small>
                        </td>
                        
                        <!-- ACCIONES -->
                        <td class="text-end pe-4">
                            <% if ("pendiente".equals(t.getEstado())) { %>
                                <form action="HistorialPedidosServlet" method="POST" onsubmit="return confirm('¿Seguro que deseas cancelar este pedido?');">
                                    <input type="hidden" name="action" value="borrar">
                                    <input type="hidden" name="idTrabajo" value="<%= t.getIdTrabajo() %>">
                                    <button type="submit" class="btn btn-outline-danger btn-sm table-action-btn" title="Cancelar Pedido">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </form>
                            <% } else { %>
                                <span class="text-muted small">-</span>
                            <% } %>
                        </td>
                    </tr>
                    <%-- Fin del for (Trabajo t : listaTrabajos) --%>
                    <% } %>
                </tbody>
            </table>
            </div>
			<%-- Fin del if (listaTrabajos == null || listaTrabajos.isEmpty()) --%>
            <% } %>
            </div>
        </div>
    </div>
</body>
</html>