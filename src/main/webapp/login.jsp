<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <%-- Establece que la página debe ser del ancho de la pantalla del usuario
    (sirve para que sea visible en cualquier tipo de pantalla) y establece el zoom por defecto en 
    100%. --%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - Fotocopiadora</title>
</head>
<body>

    <h1>Bienvenido al Sistema de Fotocopiadora</h1>
    <h2>Iniciar Sesión</h2>

    <%-- 
      Bloque de Scriptlet para manejar TODOS los mensajes.
      Tanto los errores (enviados como Atributo)
      como los éxitos (enviados como Parámetro).
    --%>
    <%
        // 1. Revisar si hay un mensaje de ERROR
        Object errorObj = request.getAttribute("error");
        if (errorObj != null) {
            String errorMsg = errorObj.toString();
            // Imprimimos el HTML del error
            out.print("<p style='color: red; font-weight: bold;'>" + errorMsg + "</p>");
        }
        
        // 2. Revisar si hay un mensaje de ÉXITO (del RegistroServlet)
        // (El servlet redirige a "login.jsp?registro=exitoso")
        String registroParam = request.getParameter("registro");
        if (registroParam != null && registroParam.equals("exitoso")) {
            // Imprimimos el HTML de éxito
            out.print("<p style='color: green; font-weight: bold;'>¡Cuenta creada exitosamente!.</p>");
        }
    %>
    <%-- 
      Este es el formulario de login.
      - 'action="LoginServlet"' define el Servlet que procesará los datos.
      - 'method="POST"' es el método usado para enviar información sensible (como contraseñas).
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