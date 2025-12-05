package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.Utils;
import clases.Usuario;
import exceptions.AuthException;
import dao.UsuarioDAO;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(description = "Servlet para el inicio de sesión", urlPatterns = { "/LoginServlet" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    // Creamos una instancia del DAO
    private UsuarioDAO usuarioDAO;
    
    // Email: Patrón estándar simple (texto + @ + texto + . + texto)
    // Evita caracteres peligrosos como < > ( ) [ ] \ , ; :
    private static final String REGEX_EMAIL = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    
    // Password: Solo letras y números (sin símbolos). Mínimo 8 caracteres.
    private static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{8,20}$";

    public LoginServlet() {
        super();
        // Inicializamos el DAO en el constructor del servlet
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * GET: redirige al JSP.
     */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Por simplicidad, si alguien intenta acceder por GET,
        // simplemente lo mandamos al login.
        response.sendRedirect("login.jsp");
	}
    
    /**
     * POST: Controla los datos de inicio de sesión y reenvia a las páginas del sistema.
     */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        
        // 1. Obtener los parámetros del formulario login.jsp
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String mensajeError = "";
        

        // 2. Lógica de login dentro de un try-catch
        try {
            // Validación 2: Email
            if (email == null || email.isBlank()) {
                mensajeError = "El email es obligatorio.";
            } else if (!Pattern.matches(REGEX_EMAIL, email)) {
                // Si no coincide con el patrón de email seguro
            	mensajeError = "El formato del email no es válido.";
            }

            // Validación 3: Contraseña
            if (password == null || password.isBlank()) {
            	mensajeError = "La contraseña es obligatoria.";
            }
    	        // Validamos caracteres inválidos
            else if (!Pattern.matches(REGEX_PASSWORD, password)) {
    	        	mensajeError = "La contraseña solo puede contener letras y números (sin símbolos).";
    	        }
            
            if (!mensajeError.isBlank()) {
            	Utils.enviarError(request, response, mensajeError, "login.jsp");
            	return;
            }
            
            // 2.1. Validar credenciales usando el DAO
            // Este método AHORA solo devuelve un Usuario o lanza AuthException
            Usuario usuario = usuarioDAO.validarLogin(email, password);
           
            // Si la línea anterior no lanzó una excepción, el login fue exitoso.
            
            // 2.2. Crear la sesión del usuario
            HttpSession session = request.getSession();
            
            // 2.3. Guardar el objeto Usuario completo en la sesión
            // (Guardamos el bean completo para usarlo en JSPs)
            session.setAttribute("usuarioLogueado", usuario);
            
            // Guardamos atributos individuales para fácil acceso en JSPs y Servlets.
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
            
        } catch (AuthException e) {
            // Capturamos CUALQUIER error de login que lanzó el DAO con el uso de una excepción personalizada
            // (Pass incorrecta, email no existe, cuenta inactiva)
            
            // Obtenemos el mensaje específico del error (ej. "La contraseña es incorrecta.")
            mensajeError = e.getMessage();
            
            // Usamos nuestra función de ayuda para enviar el error a login.jsp
            Utils.enviarError(request, response, mensajeError, "login.jsp");
        }
	}
}
