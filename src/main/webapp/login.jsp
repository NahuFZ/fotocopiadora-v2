<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 
    =================================================================
    SECCIÓN JAVA: Lógica de Negocio
    =================================================================
--%>
<%
    // 1. Preparar mensaje de ERROR
    String mensajeError = null;
    
    Object errorObj = request.getAttribute("error");
    String errorParam = request.getParameter("error");
    
    if (errorObj != null) {
    	mensajeError = errorObj.toString();
    }

    // 2. Preparar mensaje de ÉXITO
    String mensajeExito = null;
    String registroParam = request.getParameter("registro");
    String logoutParam = request.getParameter("logout");
    
    if ("exitoso".equals(registroParam)) {
        mensajeExito = "¡Cuenta creada! Por favor, inicia sesión.";
    } else if ("exitoso".equals(logoutParam)) {
        mensajeExito = "Sesión cerrada correctamente.";
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Iniciar Sesión - Fotocopiadora</title>
    <meta charset="UTF-8">
	<%-- Establece que la página debe ser del ancho de la pantalla del usuario
	    (sirve para que sea visible en cualquier tipo de pantalla) y establece el zoom por defecto en 
	    100%. --%>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<!-- 1. INTEGRAR BOOTSTRAP (CSS) -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" 
		rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
	<style>
        body {
            background-color: #f8f9fa; /* Gris muy claro de fondo */
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh; /* Altura completa de la ventana */
        }
        .login-card {
            width: 70%; /* El ancho mínimo */
            max-width: 400px; /* Ancho máximo de la tarjeta */
            padding: 20px; /* para que haya algo de borde si se achica la pantalla */
        }
    </style>
</head>
<body>

    <div class="login-card">
        
        <!-- Tarjeta con sombra -->
        <div class="card shadow border-0">
            <div class="card-body p-4">
                
                <!-- Encabezado -->
                <div class="text-center mb-4">
                    <h3 class="fw-bold text-primary">Fotocopiadora</h3>
                    <p class="text-muted">Bienvenido al sistema</p>
                </div>

			    <%-- Bloque donde mostramos las alertas --%>
	         	<!-- Mostramos el error en pantalla -->
	            <% if (mensajeError != null) { %>
                    <div class="alert alert-danger text-center" role="alert">
                        <%= mensajeError %>
                    </div>
                <% } %>
				<!-- Mostramos el mensaje de exito en pantalla -->
                <% if (mensajeExito != null) { %>
                    <div class="alert alert-success text-center" role="alert">
                        <%= mensajeExito %>
                    </div>
                <% } %>
			    
			    <%-- 
			      Este es el formulario de login.
			      - 'action="LoginServlet"' define el Servlet que procesará los datos.
			      - 'method="POST"' es el método usado para enviar información sensible (como contraseñas).
			    --%>
			    <form action="LoginServlet" method="POST">
			        <%-- mb-3: margin-bottom tamaño 3 ($spacer) --%>
			        <div class="mb-3">
			            <label for="email" class="form-label">Email:</label><br>
			            <input type="email" class="form-control" id="email" name="email" placeholder="nombre@ejemplo.com" required>
			        </div>
			        <br>
			        <div class="mb-4">
			            <label for="password" class="form-label">Contraseña:</label><br>
			            <input type="password" class="form-control" id="password" name="password" placeholder="Tu contraseña" required>
			        </div>
			        <br>
			        <div class="d-grid">
			            <button type="submit" class="btn btn-primary btn-lg">Ingresar</button>
			        </div>
			    </form>
			</div>
			
		    <!-- Pie de tarjeta -->
           	<div class="card-footer text-center py-3 bg-white border-top-0">
                <small class="text-muted">¿No tienes cuenta?</small><br>
                <a href="registro.jsp" class="fw-bold text-decoration-none">Regístrate aquí</a>
           	</div>
		</div>
	</div>
</body>
</html>