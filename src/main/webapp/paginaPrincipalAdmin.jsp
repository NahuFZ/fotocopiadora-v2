<%-- 
  BLOQUE DE SEGURIDAD OBLIGATORIO
  Esto va ANTES de cualquier HTML.
  Verifica si el usuario está logueado y si es un 'admin'.
  Si no lo es, lo expulsa al login.
--%>
<%
    // 1. Obtener la sesión actual, sin crear una nueva si no existe
    HttpSession sesion = request.getSession(false);

    String nombreRol = null;
    
    if (sesion != null) {
        // 2. Si hay sesión, obtener el rol
        nombreRol = (String) sesion.getAttribute("nombreRol");
    }

    // 3. Comprobar la lógica de permisos
    if (sesion == null || nombreRol == null || !nombreRol.equals("admin")) {
        // No hay sesión, o no hay rol, o el rol no es 'admin'
        
        // Preparamos un mensaje de error para el login
        request.setAttribute("error", "Acceso denegado. Debe ser administrador.");
        
        // Usamos forward para enviar el error a login.jsp
        request.getRequestDispatcher("login.jsp").forward(request, response);
        
        // Detenemos la ejecución del resto del JSP
        return; 
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
    <title>Panel de Administrador</title>
</head>
<body>

    <h1>Panel de Administrador</h1>
    
    <%-- Saludamos al admin por su nombre --%>
    <h3>Bienvenido, <%= nombreAdmin %></h3>
    
    <p>Seleccione una tarea para continuar:</p>
    
    <hr>
    
    <%-- 
      Para mantener la simplicidad (sin CSS), usamos formularios
      que actúan como "botones" de navegación.
    --%>
    
    <div style="margin-top: 20px;">
        
        <!-- Botón 1: Gestionar Trabajos -->
        <form action="gestionTrabajos.jsp" method="GET" style="display: inline-block;">
            <button type="submit">Gestionar Trabajos de Clientes</button>
        </form>
        
        <!-- Botón 2: Gestionar Cuentas -->
        <form action="gestionCuentas.jsp" method="GET" style="display: inline-block;">
            <button type="submit">Gestionar Cuentas de Usuarios</button>
        </form>
        
        <!-- Botón 3: Cerrar Sesión -->
        <form action="LogoutServlet" method="POST" style="display: inline-block;">
            <button type="submit">Cerrar Sesión</button>
        </form>
        
    </div>

</body>
</html>