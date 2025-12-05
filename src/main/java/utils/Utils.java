package utils;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
public class Utils {
    /**
     * Verifica que la sesión exista.
     * Verifica que el usuario logueado presente el rol de ADMINISTRADOR.
     * Si no lo hay, redirige automáticamente al login.
     * * @return true si es admin (permitir acceso), false si no lo es (enviar a login).
     * @throws  IOException, ServletException 
     */
    public static boolean esAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Solicitamos la sesión del usuario
    	HttpSession session = request.getSession(false);
        String mensajeError = null;
        
        // Comprueba que la sesión exista y que haya algún ID de usuario
        if  (!comprobarSesion(session)) {
        	mensajeError = "La sesión ha caducado";
        	
        }
        
        // Comprueba que el usuario sea un administrador
        else if (!"admin".equals(session.getAttribute("nombreRol"))) {
        	mensajeError = "Acceso no autorizado. Solo se permiten administradores";
            session.invalidate();
        }
        
        if (mensajeError != null) {
        	System.err.print("Error: " + mensajeError + System.lineSeparator());
        	request.setAttribute("error", mensajeError);
        	request.getRequestDispatcher("login.jsp").forward(request, response);
            return false; // Indicamos al Servlet que debe detenerse
        }
        return true; // Acceso concedido
    }

    /**
     * Verifica que la sesión exista.
     * Verifica que el usuario logueado presente el rol de CLIENTE.
     * Si no lo hay, redirige automáticamente al login.
     * * @return true si es cliente (permitir acceso), false si no lo es (enviar a login).
     * @throws IOException, ServletException 
     */
    public static boolean esCliente(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	 // Solicitamos la sesión del usuario
    	HttpSession session = request.getSession(false);
        String mensajeError = null;
        
        // Comprueba que la sesión exista y que haya algún ID de usuario
        if  (!comprobarSesion(session)) {
        	mensajeError = "La sesión ha caducado";
        	
        }
        
        // Comprueba que el usuario sea un administrador
        else if (!"cliente".equals(session.getAttribute("nombreRol"))) {
        	mensajeError = "Acceso no autorizado. Solo se permiten clientes";
        	session.invalidate();
        }
        
        if (mensajeError != null) {
        	System.err.print("Error: " + mensajeError + System.lineSeparator());
        	request.setAttribute("error", mensajeError);
        	request.getRequestDispatcher("login.jsp").forward(request, response);
            return false; // Indicamos al Servlet que debe detenerse
        }
        return true; // Acceso concedido
    }
    /**
     * Verifica que la sesión exista.
     * Incluso si la sesión se creó automaticamente, comprueba que el usuario se haya logueado
     * con la verificación de su ID e invalida la sesión en ese caso.
     * @return true si existe, false si no existe.
     */
    public static boolean comprobarSesion(HttpSession session) {
    	Boolean sesionExiste = true;
    	
    	// Comprueba que la sesión exista
        if (session == null) {
        	sesionExiste = false;
        }
        // Comprueba que la sesión exista mediante el idUsuario
        else if (session.getAttribute("idUsuario") == null) {
        	sesionExiste = false;
        	session.invalidate();
        }
        return sesionExiste;
    }
    
	  /**
     * Método para enviar un mensaje de error de un Servlet a un JSP para que lo ve.
     */
    public static void enviarError(HttpServletRequest request, HttpServletResponse response, String mensaje, String JSP)
            throws ServletException, IOException {

        // 1. Pone el mensaje de error en el "request" (la "mochila")
        request.setAttribute("error", mensaje);

        // 2. Reenvía al usuario (y la mochila con el error) a login.jsp
        request.getRequestDispatcher(JSP).forward(request, response);
    }
}
