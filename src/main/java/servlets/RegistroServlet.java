package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Imports de Utilidades
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//Imports de nuestra arquitectura DAO
import dao.UsuarioDAO;

/**
 * Servlet que maneja el proceso de registro de un nuevo cliente.
 * Utiliza UsuarioDAO para interactuar con la BBDD.
 */
@WebServlet(description = "Servlet para registrar a un nuevo usuario", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// Creamos una instancia del DAO.
    // En aplicaciones más avanzadas, esto se "inyectaría".
    // Para este proyecto, instanciarlo aquí está perfecto.
    private UsuarioDAO usuarioDAO;

    public RegistroServlet() {
        super();
        // Inicializamos el DAO en el constructor del servlet
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Si alguien intenta acceder por GET, simplemente lo mandamos al formulario.
        response.sendRedirect("registro.jsp");
	}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtener los parámetros del formulario registro.jsp
        String nombreCompleto = request.getParameter("nombre_completo");
        String email = request.getParameter("email");
        String pass1 = request.getParameter("password");
        String pass2 = request.getParameter("password_confirm");

        // 2. Validación del Lado del Servidor
        List<String> errores = new ArrayList<>();

        // Validación 1: Nombre completo obligatorio (nuevo requisito)
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            errores.add("El nombre completo es obligatorio.");
        }

        // Validación 2: Email
        if (email == null || email.isBlank()) {
            errores.add("El email es obligatorio.");
        } else if (!email.contains("@") || !email.contains(".")) {
            // Validación simple de formato
            errores.add("El formato del email no es válido.");
        }

        // Validación 3: Contraseña
        if (pass1 == null || pass1.isBlank()) {
            errores.add("La contraseña es obligatoria.");
        } else if (pass1.length() < 8) {
            // Es buena idea mantener una regla de longitud mínima
            errores.add("La contraseña debe tener al menos 8 caracteres.");
        }

        // Validación 4: Coincidencia de contraseñas
        else if (!pass1.equals(pass2)) {
            errores.add("Las contraseñas no coinciden.");
        }

        // 3. Decidir si continuar o mostrar errores
        if (!errores.isEmpty()) {
            // Si hay errores de validación, no conectamos a la BBDD.
            // Reenviamos al usuario al formulario con la lista de errores.
        	enviarErrores(request, response, errores);
            return; // Detenemos la ejecución
        }
        
        // 4. Si las validaciones básicas pasaron, usamos el DAO
        
        try {
            // Validación 5: Email ya existe (Lógica de BBDD)
            // Llamamos al método del DAO
            if (usuarioDAO.emailExiste(email)) {
                errores.add("El email ingresado ya se encuentra registrado.");
                enviarErrores(request, response, errores);
                return;
            }
            
            // 5. Crear el usuario (Lógica de BBDD)
            // Llamamos al método del DAO
            boolean registroExitoso = usuarioDAO.crearCliente(nombreCompleto, email, pass1);

            if (registroExitoso) {
                // ¡Registro exitoso!
                // Redirigimos al login con un mensaje de éxito.
                response.sendRedirect("login.jsp?registro=exitoso");
            } else {
                // Error raro (el DAO devolvió false)
                errores.add("Ocurrió un error inesperado al crear la cuenta.");
                enviarErrores(request, response, errores);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            // Captura de cualquier otro error (ej. BBDD caída, Driver no encontrado)
            e.printStackTrace(); // Imprime el error en la consola de Tomcat
            errores.add("Error de conexión con la base de datos. Intente más tarde.");
            enviarErrores(request, response, errores);
        }
    }

    /**
     * Método de ayuda para reenviar al usuario a registro.jsp con una lista de errores.
     */
    private void enviarErrores(HttpServletRequest request, HttpServletResponse response, List<String> errores)
            throws ServletException, IOException {
        
        // Guardamos la lista de errores para que registro.jsp la muestre
        request.setAttribute("listaErrores", errores);
        
        // También reenviamos los datos que el usuario ya había escrito
        request.setAttribute("nombreAnterior", request.getParameter("nombre_completo"));
        request.setAttribute("emailAnterior", request.getParameter("email"));

        // Reenviamos al usuario DE VUELTA al formulario de registro
        request.getRequestDispatcher("registro.jsp").forward(request, response);
    }
    
}
