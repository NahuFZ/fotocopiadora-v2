<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Inicio de sesión</title>
</head>
<body>
	<h2>Formulario de contacto</h2>
    <form action="ProcesarFormulario" method="post">
        Nombre: <input type="text" name="nombre"><br>
        Email: <input type="email" name="email"><br>
        <input type="submit" value="Enviar">
    </form>
</body>
</html>