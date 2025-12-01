<%@ page import="utils.Utils"%>
<%@ page import="java.util.List"%>
<%@ page import="java.time.LocalDate"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%-- 
    =================================================================
    SECCIÓN JAVA: Seguridad y Datos
    =================================================================
--%>
<%
// ---- SEGURIDAD ----
// Comprueba sesión abierta de cliente
	if (!Utils.esCliente(request, response)) {
		return;
	}

// Obtener la sesión actual, sin crear una nueva si no existe
HttpSession sesion = request.getSession(false);
// Buscamos el nombre del usuario
String nombreCliente = (String) sesion.getAttribute("nombreCompleto");

// ---- RECUPERAR ERRORES ----

Object erroresObj = request.getAttribute("listaErrores");
List<String> listaErrores = null;

if (erroresObj != null && erroresObj instanceof List) {
	listaErrores = (List<String>) erroresObj;
}

// ---- RECUPERAR EXITO ----
String exitoParam = request.getParameter("exito");

// Fecha mínima (Hoy). Usamos la fecha local del servidor para simplificar.
LocalDate hoy = LocalDate.now();
String fechaMinima = hoy.format(DateTimeFormatter.ISO_LOCAL_DATE);
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Nuevo Pedido - Fotocopiadora</title>
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
body {
	background-color: #f8f9fa;
}

.main-card {
	max-width: 800px; /* Ancho máximo ideal para lectura */
	margin: 0 auto; /* Centrado horizontal automático */
}
</style>
</head>
<body>
	<!-- BARRA DE NAVEGACIÓN -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
		<div class="container">
			<%-- Lado izquierdo: pequeño logo --%>
			<a class="navbar-brand fw-bold" href="paginaPrincipalCliente.jsp">
				<!-- Insertamos un icono de flecha hacia la izquierda --> 
				<i class="bi bi-arrow-left-circle me-2"></i>Volver al Panel
			</a>
			<%-- Lado derecho: Nombre del usuario y cerrar sesión --%>
			<div>
				<span class="navbar-text text-white mx-3"> <strong>Usuario: <%=nombreCliente%></strong></span>
				<form action="LogoutServlet" method="POST" class="d-inline">
	                <button type="submit" class="btn btn-outline-light btn-sm">
	                    <i class="bi bi-box-arrow-right me-1"></i> Cerrar Sesión
	                </button>
		        </form>
	        </div>
		</div>
	</nav>

	<div class="container">
		<div class="card shadow-sm border-0 main-card">
			<div class="card-body p-4">
				<!-- CABECERA -->
				<div class="text-center mb-4">
					<h4 class="card-title text-primary">
						<i class="bi bi-cloud-upload me-2"></i>Cargar Nuevo Pedido
					</h4>
					<p class="text-muted small">Complete los detalles de su
						impresión</p>
				</div>
				
				<!-- Mensajes de Error/Éxito -->
                <% if (listaErrores != null && !listaErrores.isEmpty()) { %>
                    <div class="alert alert-danger">
                        <ul class="mb-0 ps-3">
                            <% for (String error : listaErrores) { %> <li><%= error %></li> <% } %>
                        </ul>
                    </div>
                <% } %>
                
                <% if ("true".equals(exitoParam)) { %>
                    <div class="alert alert-success d-flex align-items-center">
                        <i class="bi bi-check-circle-fill fs-4 me-3"></i>
                        <div><strong>¡Pedido enviado!</strong> Ver en <a href="HistorialPedidosServlet" class="alert-link">historial</a>.</div>
                    </div>
                <% } %>
				
				<!-- FORMULARIO -->
				<%-- 
			      Este formulario debe tener method="POST" para poder enviar archivos
			      y enctype="multipart/form-data" que es OBLIGATORIO para <input type="file">.
			      Ese atributo le dice al servidor que se prepare para recibir un archivo, no solo texto.
			    --%>
				<form action="NuevoPedidoServlet" method="POST"
					enctype="multipart/form-data">

					<!-- 1. ARCHIVO -->
					<div class="mb-4">
						<label for="archivo" class="form-label fw-bold">1.
							Seleccione el archivo</label> <input type="file"
							class="form-control" id="archivo" name="archivo"
							accept=".pdf,.jpg,.jpeg,.png" required>
						<div class="form-text">Formatos permitidos: PDF, JPG, PNG.
							Máximo 10MB.</div>
					</div>

					<div class="row g-3 mb-4">
						<!-- 2. COPIAS -->
						<div class="col-md-4">
							<label for="num_copias" class="form-label fw-bold">2.
								Número de copias</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-files"></i></span>
								<input type="number" class="form-control" id="num_copias"
									name="num_copias" min="1" max="99" value="1" required>
							</div>
							<div class="form-text">Se permiten de 1 a 99 copias.</div>
						</div>

						<!-- 3. CALIDAD -->
						<div class="col-md-4">
							<label for="calidad" class="form-label fw-bold">3.
								Calidad de impresión</label> <select class="form-select" id="calidad"
								name="calidad">
								<option value="blanco_y_negro">Blanco y Negro</option>
								<option value="color">Color</option>
							</select>
						</div>

						<!-- 4. FAZ -->
						<div class="col-md-4">
							<label for="faz" class="form-label fw-bold">4. Tipo de
								Faz</label> <select class="form-select" id="faz" name="faz">
								<option value="simple">Simple Faz</option>
								<option value="doble">Doble Faz</option>
							</select>
						</div>

					</div>

					<!-- 5. FECHA -->
					<div class="mb-4">
						<label for="fecha_retiro" class="form-label fw-bold">5.
							¿Cuándo desea retirar?</label> <input type="date" class="form-control"
							id="fecha_retiro" name="fecha_retiro" min="<%=fechaMinima%>"
							required>
					</div>

					<hr class="my-4">

					<div class="d-grid">
						<button type="submit" class="btn btn-primary btn-lg">
							<i class="bi bi-send-fill me-2"></i>Enviar Pedido
						</button>
					</div>

				</form>
			</div>
		</div>
	</div>

</body>
</html>