<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 
    =================================================================
    SECCIÓN JAVA: Lógica de Negocio
    =================================================================
--%>
<%
    // Recuperar datos previos (para rellenar el formulario si hubo error)
    String nombrePrevio = (String) request.getAttribute("nombreAnterior");
    String emailPrevio = (String) request.getAttribute("emailAnterior");
    
    // Asegurarnos de que no sean null para imprimirlos en el HTML
    if (nombrePrevio == null) nombrePrevio = "";
    if (emailPrevio == null) emailPrevio = "";

    // Recuperar la lista de errores
    List<String> listaErrores = null;
    Object erroresObj = request.getAttribute("listaErrores");
    
    if (erroresObj != null && erroresObj instanceof List) {
        listaErrores = (List<String>) erroresObj;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Cliente - Fotocopiadora</title>
    <!-- Integramos BOOTSTRAP (CSS) -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" 
		rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    
    <style>
        body {
            background-color: #f8f9fa;
            min-height: 100vh; /* min-height permite scroll si la tarjeta es muy alta */
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px 0; /* Un poco de aire arriba y abajo en móviles */
        }
        .register-card {
            max-width: 500px; /* Un poco más ancha que el login */
            width: 100%;
        }
    </style>
</head>
<body>
    <div class="register-card">
        <div class="card shadow border-0">
            <div class="card-body p-4">
                
                <!-- Encabezado -->
                <div class="text-center mb-4">
                    <h3 class="fw-bold text-primary">Crear Cuenta</h3>
                    <p class="text-muted">Regístrate para solicitar impresiones</p>
                </div>

                <!-- Lista de Errores -->
                <% if (listaErrores != null && !listaErrores.isEmpty()) { %>
                    <div class="alert alert-danger" role="alert">
                        <ul class="mb-0 ps-3">
                            <% for (String error : listaErrores) { %>
                                <li><%= error %></li>
                            <% } %>
                        </ul>
                    </div>
                <% } %>
                
                <!-- Formulario -->
			    <form action="RegistroServlet" method="POST">
			        
			        <!-- Nombre Completo -->
			        <div class="mb-3">
			            <label for="nombre"class="form-label">Nombre Completo:</label>
			            <input type="text" class="form-control" id="nombre" name="nombre_completo" 
                               placeholder="Ingrese su nombre" value="<%= nombrePrevio %>" required>
			        </div>
			        
			        <!-- Email -->
			        <div class="mb-3">
			            <label for="email" class="form-label">Email:</label>
			            <input type="email" class="form-control" id="email" name="email" 
                               placeholder="nombre@ejemplo.com" value="<%= emailPrevio %>" required>
			        </div>
			        
			        <!-- Contraseña -->
			        <div class="mb-3">
			            <label for="password">Contraseña:</label><br>
			            <input type="password" class="form-control" id="password" name="password" 
			            	placeholder="Ingrese su contraseña" required>
			        	<div class="form-text">
						La contraseña debe tener como minimo 8 caracteres.
						</div>
			        </div>
			        
			        <!-- Confirmar Contraseña -->
			        <div class="mb-4">
			            <label for="password_confirm" class="form-label">Confirmar Contraseña:</label><br>
			            <input type="password" class="form-control" id="password_confirm" name="password_confirm" 
			            placeholder="Repite tu contraseña" required>
			        </div>
			        
                    <!-- Botón de enviar formulario -->
			        <div class="d-grid">
			            <button type="submit" class="btn btn-primary btn-lg">Crear Cuenta</button>
			        </div>
			    </form>
			</div>
			
		    <%-- Enlace para volver al inicio de sesión --%>
            <div class="card-footer text-center py-3 bg-white border-top-0">
                <small class="text-muted">¿Ya tienes una cuenta?</small><br>
                <a href="login.jsp" class="fw-bold text-decoration-none">Inicia sesión aquí</a>
            </div>
			
		</div>
	</div>
</body>
</html>