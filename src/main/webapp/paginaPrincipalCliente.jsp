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
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Cliente</title>
</head>
<body>

    <h1>Panel de Cliente</h1>
    
    <%-- Saludamos al cliente por su nombre --%>
    <h3>Bienvenido, <%= nombreCliente %></h3>
    <p>¿Qué te gustaría hacer?</p>
    
    <hr>
    
    <%-- 
      Para mantener la simplicidad (sin CSS), usamos formularios
      que actúan como "botones" de navegación.
      El 'style' es solo para que los botones queden uno al lado del otro.
    --%>
    
    <div style="margin-top: 20px;">
        
        <!-- Botón 1: Nuevo Pedido (tu "reservar nuevas fotocopias") -->
        <form action="nuevoPedido.jsp" method="GET" style="display: inline-block;">
            <button type="submit">Solicitar Nuevas Fotocopias (Nuevo Pedido)</button>
        </form>
        
        <!-- Botón 2: Historial de Pedidos -->
        <!-- Apunta a HistorialServlet para que la tabla se muestra al apretar el botón -->
        <form action="HistorialPedidosServlet" method="GET" style="display: inline-block;">
            <button type="submit">Ver Historial de Pedidos</button>
        </form>
        
        <!-- Botón 3: Cerrar Sesión -->
        <form action="LogoutServlet" method="POST" style="display: inline-block;">
            <button type="submit">Cerrar Sesión</button>
        </form>
        
    </div>

</body>
</html>
