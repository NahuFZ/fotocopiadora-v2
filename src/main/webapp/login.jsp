<%-- 
  Esta línea importa la JSTL (Java Standard Tag Library).
  Es necesaria para usar la lógica <c:if> que mostrará los errores.
  Deberás asegurarte de tener la dependencia de JSTL en tu proyecto.
  
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <%-- Establece que la página debe ser del ancho de la pantalla del usuario
    (sirve para que sea visible en teléfonos) y establece el zoom por defecto en 
    100%. --%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - Fotocopiadora</title>
</head>
<body>

    <h1>Bienvenido al Sistema de Fotocopiadora</h1>
    <h2>Iniciar Sesión</h2>

    <%-- 
      Esta sección es para mostrar mensajes de error.
      Si el LoginServlet falla, redirigirá de nuevo a esta página
      estableciendo un atributo "error".
    
    <c:if test="${not empty error}">
        
          Aunque dijimos "sin CSS", un simple color rojo para un error
          es una excepción de usabilidad fundamental.
          Si el profesor es estricto, puedes quitar el 'style'.
       
        <p style="color: red; font-weight: bold;">
            <c:out value="${error}" />
        </p>
    </c:if>

    
      Esta sección es para un mensaje de éxito, por ejemplo,
      cuando un usuario se registra y es redirigido aquí.
    
    <c:if test="${not empty exito}">
        <p style="color: green; font-weight: bold;">
            <c:out value="${exito}" />
        </p>
    </c:if>
	--%>

    <%-- 
      Este es el formulario de login.
      - 'action="LoginServlet"' define el Servlet que procesará los datos.
      - 'method="POST"' es fundamental para enviar contraseñas de forma segura.
    --%>
    <form action="LoginServlet" method="POST">
        <div>
            <label for="email">Email:</label><br>
            <input type="email" id="email" name="email" required>
        </div>
        <br>
        <div>
            <label for="password">Contraseña:</label><br>
            <input type="password" id="password" name="password" required>
        </div>
        <br>
        <div>
            <button type="submit">Ingresar</button>
        </div>
    </form>

    <hr>

    <%-- 
      Este es el enlace a la página de registro,
      tal como estaba en tu diagrama de flujo.
    --%>
    <p>¿No tienes una cuenta de cliente?</p>
    <a href="registro.jsp">Regístrate aquí</a>

</body>
</html>