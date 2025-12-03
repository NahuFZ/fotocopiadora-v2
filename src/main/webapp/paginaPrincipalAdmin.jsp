<%@ page import="utils.Utils" %>
<%-- 
  BLOQUE DE SEGURIDAD OBLIGATORIO
  Esto va ANTES de cualquier HTML.
  Verifica si el usuario está logueado y si es un 'admin'.
  Si no lo es, lo expulsa al login.
--%>
<%
	//Comprueba sesión abierta por un administrador.
	if (!Utils.esAdmin(request, response)) {
		return;
	}

	// 1. Obtener la sesión actual, sin crear una nueva si no existe
    HttpSession sesion = request.getSession(false);
    String nombreRol = null;
    
    if (sesion != null) {
        // 2. Si hay sesión, obtener el rol
        nombreRol = (String) sesion.getAttribute("nombreRol");
    }
    
    // Si llegamos aquí, el usuario es un admin validado.
    // Obtenemos su nombre para saludarlo.
    String nombreAdmin = (String) sesion.getAttribute("nombreCompleto");
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Administrador - Fotocopiadora</title>
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
        
        .admin-card {
            transition: transform 0.2s, box-shadow 0.2s;
            cursor: pointer;
            height: 100%;
            border-left: 5px solid; /* Borde lateral de color para distinguir */
        }
        .admin-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
        }
        
        /* Colores específicos para cada tarjeta */
        .card-trabajos { border-left-color: #0d6efd; } /* Azul */
        .card-trabajos .card-icon { color: #0d6efd; }
        
        .card-cuentas { border-left-color: #198754; } /* Verde */
        .card-cuentas .card-icon { color: #198754; }
        
        .card-icon {
            font-size: 3rem;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>

    <!-- Barra de navegación (Oscura para diferenciar del cliente) -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-5">
        <div class="container">
            <div class="navbar-brand fw-bold">
                <i class="bi bi-shield-lock-fill me-2"></i>Administración
            </div>
            <div class="navbar-text">
               <span class="text-white-50 mx-3">Sesión: <strong class="text-white"><%= nombreAdmin %></strong></span>
               <%-- Botón para cerrar sesión --%>
               <form action="LogoutServlet" method="POST" class="d-inline">
                   <button type="submit" class="btn btn-outline-light btn-sm">
                       <i class="bi bi-box-arrow-right me-1"></i> Salir
                   </button>
               </form>
            </div>
        </div>
    </nav>

    <!-- CONTENIDO -->
    <div class="container">
        
        <div class="text-center mb-5">
            <h2 class="fw-light">Panel de Control</h2>
            <p class="text-muted">Seleccione el módulo que desea gestionar</p>
        </div>

        <div class="row justify-content-center g-4">
            
            <!-- MÓDULO 1: GESTIÓN DE TRABAJOS -->
            <div class="col-md-5 col-lg-4">
                <!-- El formulario envuelve toda la tarjeta para hacerla clickeable -->
                <form action="GestionTrabajosServlet" method="GET" style="height: 100%;">
                    <!-- Nota: El action apunta al SERVLET, no al JSP -->
                    
                    <button type="submit" class="text-decoration-none border-0 bg-transparent w-100 p-0" style="text-align: inherit;">
                        <div class="card admin-card card-trabajos shadow-sm">
                            <div class="card-body text-center p-4">
                                <div class="card-icon">
                                    <i class="bi bi-layers-fill"></i>
                                </div>
                                <h4 class="card-title text-dark mb-3">Trabajos</h4>
                                <p class="card-text text-muted">
                                    Ver pedidos de clientes, descargar archivos y actualizar estados (Terminado/Retirado).
                                </p>
                                <div class="mt-3 text-primary fw-bold">
                                    Ingresar <i class="bi bi-arrow-right"></i>
                                </div>
                            </div>
                        </div>
                    </button>
                </form>
            </div>

            <!-- MÓDULO 2: GESTIÓN DE CUENTAS -->
            <div class="col-md-5 col-lg-4">
                <form action="GestionCuentasServlet" method="GET" style="height: 100%;">
                    
                    <button type="submit" class="text-decoration-none border-0 bg-transparent w-100 p-0" style="text-align: inherit;">
                        <div class="card admin-card card-cuentas shadow-sm">
                            <div class="card-body text-center p-4">
                                <div class="card-icon">
                                    <i class="bi bi-people-fill"></i>
                                </div>
                                <h4 class="card-title text-dark mb-3">Usuarios</h4>
                                <p class="card-text text-muted">
                                    Administrar cuentas registradas, cambiar roles (Cliente/Admin) y bloquear accesos.
                                </p>
                                <div class="mt-3 text-success fw-bold">
                                    Ingresar <i class="bi bi-arrow-right"></i>
                                </div>
                            </div>
                        </div>
                    </button>
                </form>
            </div>
            
        </div>
    </div>

</body>
</html>
