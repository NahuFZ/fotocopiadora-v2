package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import clases.Usuario;
import dao.AuthException;
import dao.UsuarioDAO;

import java.io.IOException;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(description = "Servlet para el inicio de sesión", urlPatterns = { "/LoginServlet" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    // Creamos una instancia del DAO
    private UsuarioDAO usuarioDAO;

    public LoginServlet() {
        super();
        // Inicializamos el DAO en el constructor del servlet
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Maneja peticiones GET. Lo redirigimos a POST, aunque
     * idealmente debería solo mostrar el formulario.
     */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Por simplicidad, si alguien intenta acceder por GET,
        // simplemente lo mandamos al login.
        response.sendRedirect("login.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        
        // 1. Obtener los parámetros del formulario login.jsp
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 2. Lógica de login dentro de un try-catch
        try {
            
            // --- INICIO DEL "CAMINO FELIZ" ---
            
            // 2.1. Validar credenciales usando el DAO
            // Este método AHORA solo devuelve un Usuario o lanza AuthException
            Usuario usuario = usuarioDAO.validarLogin(email, password);
           
            // Si la línea anterior no lanzó una excepción, el login fue exitoso.
            
            // 2.2. Crear la sesión del usuario
            HttpSession session = request.getSession();
            
            // 2.3. Guardar el objeto Usuario completo en la sesión
            // (Guardamos el bean completo para usarlo en JSPs)
            session.setAttribute("usuarioLogueado", usuario);
            
            // Guardamos atributos individuales para fácil acceso en JSPs con Scriptlets <% ... %>
            session.setAttribute("idUsuario", usuario.getIdUsuario());
            session.setAttribute("nombreCompleto", usuario.getNombreCompleto());
            session.setAttribute("nombreRol", usuario.getNombreRol());

            // 2.4. Redirigir según el rol
            if ("admin".equals(usuario.getNombreRol())) {
                // Es Admin
                response.sendRedirect("paginaPrincipalAdmin.jsp");
            } else {
                // Es Cliente
                response.sendRedirect("paginaPrincipalCliente.jsp");
            }

            // --- FIN DEL "CAMINO FELIZ" ---
            
        } catch (AuthException e) {
            
            // --- CAMINO DE ERROR (Autenticación) ---
            // Capturamos CUALQUIER error de login que lanzó el DAO
            // (Pass incorrecta, email no existe, cuenta inactiva)
            
            // Obtenemos el mensaje específico del error (ej. "La contraseña es incorrecta.")
            String mensajeError = e.getMessage();
            
            // Usamos nuestra función de ayuda para enviar el error a login.jsp
            enviarError(request, response, mensajeError);
        } 
        // (Dejamos que otros errores, como ServletException, se propaguen)
	}
	  /**
     * Método de ayuda para reenviar al usuario a login.jsp con un mensaje de error.
     */
    private void enviarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {

        // 1. Pone el mensaje de error en el "request" (la "mochila")
        request.setAttribute("error", mensaje);

        // 2. Reenvía al usuario (y la mochila con el error) a login.jsp
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
