package utils;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
public class Utils {
    /**
     * Verifica si hay un usuario logueado con rol de ADMINISTRADOR.
     * Si no lo hay, redirige automáticamente al login.
     * * @return true si es admin (permitir acceso), false si no lo es (detener ejecución).
     * @throws  IOException, ServletException 
     */
    public static boolean esAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Solicitamos la sesión del usuario
    	HttpSession session = request.getSession(false);
        String mensajeError = null;
        
        // Comprueba que la sesión exista y que haya algún ID de usuario
        if  (session == null || session.getAttribute("idUsuario") == null) {
        	mensajeError = "La sesión ha caducado / no es valida";
        	
        }
        
        // Comprueba que el usuario sea un administrador
        else if (!"admin".equals(session.getAttribute("nombreRol"))) {
        	mensajeError = "Acceso no autorizado. Solo se permiten administradores";
        }
        
        if (mensajeError != null) {
        	System.err.print("Error: " + mensajeError);
        	request.setAttribute("error", mensajeError);
        	request.getRequestDispatcher("login.jsp").forward(request, response);
            session.invalidate();
            return false; // Indicamos al Servlet que debe detenerse
        }
        return true; // Acceso concedido
    }

    /**
     * Verifica si hay un usuario logueado con rol de CLIENTE.
     * Si no lo hay, redirige automáticamente al login.
     * * @return true si es cliente, false si no.
     * @throws IOException, ServletException 
     */
    public static boolean esCliente(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	 // Solicitamos la sesión del usuario
    	HttpSession session = request.getSession(false);
        String mensajeError = null;
        
        // Comprueba que la sesión exista y que haya algún ID de usuario
        if  (session == null || session.getAttribute("idUsuario") == null) {
        	mensajeError = "La sesión ha caducado / no es valida";
        }
        
        // Comprueba que el usuario sea un administrador
        else if (!"cliente".equals(session.getAttribute("nombreRol"))) {
        	mensajeError = "Acceso no autorizado. Solo se permiten clientes";
        }
        
        if (mensajeError != null) {
        	System.err.print("Error: " + mensajeError);
        	request.setAttribute("error", mensajeError);
        	request.getRequestDispatcher("login.jsp").forward(request, response);
            session.invalidate();
            return false; // Indicamos al Servlet que debe detenerse
        }
        return true; // Acceso concedido
    }
    
    /**
     * Obtiene el ID del usuario actual de forma segura.
     * (Asume que ya se validó el acceso con esAdmin o esCliente).
     */
    public static int getIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("idUsuario") != null) {
            return (Integer) session.getAttribute("idUsuario");
        }
        return -1; // O lanzar excepción
    }
    /**
     * Método de ayuda para reenviar al usuario a registro.jsp con una lista de errores.
     
    public static void enviarErrores(HttpServletRequest request, HttpServletResponse response, List<String> errores)
            throws ServletException, IOException {
        
        // Guardamos la lista de errores para que registro.jsp la muestre
        request.setAttribute("listaErrores", errores);
        
        // También reenviamos los datos que el usuario ya había escrito
        request.setAttribute("nombreAnterior", request.getParameter("nombre_completo"));
        request.setAttribute("emailAnterior", request.getParameter("email"));

        // Reenviamos al usuario DE VUELTA al formulario de registro
        request.getRequestDispatcher("registro.jsp").forward(request, response);
    }*/
}
