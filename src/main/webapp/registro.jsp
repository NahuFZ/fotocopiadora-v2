
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Cliente - Fotocopiadora</title>
</head>
<body>

    <h1>Registro de Nueva Cuenta de Cliente</h1>

     <%-- 
      Bloque de Scriptlet para mostrar la lista de errores.
      Si el RegistroServlet encuentra un problema (ej. email duplicado),
      recargará esta página y enviará un atributo "listaErrores".
    --%>
    <%
        Object erroresObj = request.getAttribute("listaErrores");
        
        // Comprobamos si el atributo existe Y si es una Lista
        if (erroresObj != null && erroresObj instanceof List) {
            
            // Hacemos el "casting" (conversión) del objeto a una Lista de Strings
            List<String> listaErrores = (List<String>) erroresObj;
            
            if (!listaErrores.isEmpty()) {
                // Si la lista no está vacía, empezamos a imprimir el HTML
    %>
                <div style="color: red; border: 1px solid red; padding: 10px; margin-bottom: 15px;">
                    <strong>Error al registrar:</strong>
                    <ul>
    <%
                        // Iteramos sobre la lista e imprimimos cada error
                        for (String error : listaErrores) {
                            out.print("<li>" + error + "</li>");
                        }
    %>
                    </ul>
                </div>
    <%
            } // Fin del if (!listaErrores.isEmpty())
        } // Fin del if (erroresObj != null)
    %>
    <form action="RegistroServlet" method="POST">
        
        <div>
            <label for="nombre">Nombre Completo:</label><br>
            <%-- 
              Este 'value' es un truco de usabilidad:
              Si el formulario falla, el servlet nos devuelve el valor
              que el usuario ya había escrito, para que no lo pierda.
            <input type="text" id="nombre" name="nombre_completo" value="<c:out value='${param.nombre_completo}' />" required>
            --%>
            <input type="text" id="nombre" name="nombre_completo" required>
        </div>
        <br>
        <div>
            <label for="email">Email:</label><br>
<%--             <input type="email" id="email" name="email" value="<c:out value='${param.email}' />" required> --%>
            <input type="email" id="email" name="email" required>
        </div>
        <br>
        <div>
            <label for="password">Contraseña:</label><br>
            <input type="password" id="password" name="password" required>
        </div>
        <br>
        <div>
            <label for="password_confirm">Confirmar Contraseña:</label><br>
            <input type="password" id="password_confirm" name="password_confirm" required>
        </div>
        <br>
        <div>
            <button type="submit">Crear Cuenta</button>
        </div>
    </form>

    <hr>

    <%-- Enlace para volver al inicio de sesión --%>
    <p>¿Ya tienes una cuenta?</p>
    <a href="login.jsp">Inicia sesión aquí</a>

</body>
</html>