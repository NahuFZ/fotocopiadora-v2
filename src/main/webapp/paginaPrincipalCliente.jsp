<%@ page import="utils.Utils" %>
<%-- 
  BLOQUE DE SEGURIDAD OBLIGATORIO
  Esto va ANTES de cualquier HTML.
  Verifica si el usuario está logueado y si es un 'cliente'.
  Si no lo es, lo expulsa al login.
--%>
<%
	// 1. Comprueba sesión abierta de cliente
	if (!Utils.esCliente(request, response)) {
		return;
	}

    // 2. Obtener la sesión actual, sin crear una nueva si no existe
    HttpSession sesion = request.getSession(false);
    String nombreRol = (String) sesion.getAttribute("nombreRol");
    
    // Si llegamos aquí, el usuario es un CLIENTE validado.
    // Obtenemos su nombre para saludarlo.
    String nombreCliente = (String) sesion.getAttribute("nombreCompleto");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<!-- CABECERA -->
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Cliente - Fotocopiadora</title>
    <!-- Integramos BOOTSTRAP (CSS) -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" 
		rel="stylesheet" integrity ="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <!-- Integramos íconos de Bootstrap -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
	   
    <style>
        body {
            background-color: #f8f9fa;
        }
        .action-card {
            transition: transform 0.2s;
            cursor: pointer;
            height: 100%; /* Para que ambas tarjetas tengan la misma altura */
        }
        .action-card:hover {
            transform: translateY(-5px); /* Efecto de "levantarse" al pasar el mouse */
            box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
        }
        .card-icon {
            font-size: 3rem;
            color: #0d6efd; /* Azul Bootstrap */
            margin-bottom: 15px;
        }
    </style>
</head>
<body>

    <!-- BARRA DE NAVEGACIÓN SUPERIOR -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-5">
        <div class="container">
        	<%-- Lado izquierdo: pequeño logo --%>
            <a class="navbar-brand fw-bold"><i class="bi bi-printer-fill me-2"></i>Fotocopiadora</a>
            
            <%-- Lado derecho: Nombre del usuario y cerrar sesión --%>
            <div class="navbar-text">
	            <span class="text-white mx-3">¡Hola, <strong><%= nombreCliente %></strong>!</span>
	            <%-- Botón para cerrar sesión --%>
	            <form action="LogoutServlet" method="POST" class="d-inline">
	                <button type="submit" class="btn btn-outline-light btn-sm">
	                    <i class="bi bi-box-arrow-right me-1"></i> Cerrar Sesión
	                </button>
	            </form>
            </div>
        </div>
    </nav>
    
    <%-- 
      Para mantener la simplicidad (sin CSS), usamos formularios
      que actúan como "botones" de navegación.
      El 'style' es solo para que los botones queden uno al lado del otro.
    --%>
      <!-- CONTENIDO PRINCIPAL -->
    <div class="container">
        
        <div class="text-center mb-5">
            <h2 class="fw-light">¿Qué te gustaría hacer hoy?</h2>
        </div>

        <div class="row justify-content-center g-4">
            
            <!-- TARJETA 1: NUEVO PEDIDO -->
            <div class="col-md-5 col-lg-4">
                <div class="card action-card shadow-sm border-0 text-center p-4">
                    <div class="card-body">
                        <div class="card-icon">
                            <i class="bi bi-file-earmark-plus"></i>
                        </div>
                        <h4 class="card-title mb-3">Nuevo Pedido</h4>
                        <p class="card-text text-muted mb-4">
                            Sube tus archivos PDF o imágenes, elige la cantidad de copias y cuándo retirarlas.
                        </p>
                        <!-- Usamos un formulario GET para mantener la lógica original -->
                        <form action="nuevoPedido.jsp" method="GET">
                            <button type="submit" class="btn btn-primary w-100">
                                Solicitar Ahora
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- TARJETA 2: HISTORIAL DE PEDIDOS -->
            <div class="col-md-5 col-lg-4">
                <div class="card action-card shadow-sm border-0 text-center p-4">
                    <div class="card-body">
                        <div class="card-icon">
                            <i class="bi bi-clock-history"></i>
                        </div>
                        <h4 class="card-title mb-3">Mis Pedidos</h4>
                        <p class="card-text text-muted mb-4">
                            Revisa el estado de tus trabajos, descarga tus archivos o cancela pedidos pendientes.
                        </p>
                        <!-- Usamos un formulario GET apuntando al SERVLET (Corrección que hicimos antes) -->
                        <form action="HistorialPedidosServlet" method="GET">
                            <button type="submit" class="btn btn-primary w-100">
                                Ver Historial
                            </button>
                        </form>
                    </div>
                </div>
            </div>
            
        </div>
    </div>  

</body>
</html>
