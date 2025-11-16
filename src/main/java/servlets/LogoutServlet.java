package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Maneja tanto GET como POST para el logout.
     * Así funciona si el usuario accede por un link (GET)
     * o un botón de formulario (POST).
     */
    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtener la sesión actual.
        // El parámetro false espara que no cree una nueva sesión si no existe.
        HttpSession session = request.getSession(false);
        
        // 2. Verificar si el usuario realmente tenía una sesión
        if (session != null) {
            // 3. Invalidar (destruir) la sesión.
            // Esto borra todos los atributos (ej. "usuarioLogueado")
            session.invalidate();
        }
        
        // 4. Redirigir al usuario a la página de login.
        // Añadimos un parámetro para que el JSP (opcionalmente)
        // pueda mostrar un mensaje de "Cierre de sesión exitoso".
        response.sendRedirect("login.jsp?logout=exitoso");
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		cerrarSesion(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		cerrarSesion(request, response);
	}

}
