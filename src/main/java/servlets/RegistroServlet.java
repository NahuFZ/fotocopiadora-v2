package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Imports de JDBC
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Imports de Utilidades
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet que maneja el proceso de registro de un nuevo cliente.
 * Recibe los datos del formulario de registro.jsp.
 */
@WebServlet(description = "Servlet para registrar a un nuevo usuario", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	// --- Configuración de la Base de Datos ---
    // ¡¡ CAMBIA ESTO !! Pon tu usuario y contraseña de MySQL.
    private String jdbcURL = "jdbc:mysql://localhost:3306/fotocopiadora?ServerTimezone=UTC";
    private String jdbcUsername = "root";
    private String jdbcPassword = ""; // Escribe tu contraseña aquí si tienes una

    public RegistroServlet() {
        super();
        // TODO Auto-generated constructor stub
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
        
        // 4. Si las validaciones básicas pasaron, conectamos a la BBDD
        Connection conn = null;
        PreparedStatement psCheck = null; // Para chequear el email
        PreparedStatement psInsert = null; // Para insertar el usuario
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            // 5. Validación de BBDD: Verificar si el email ya existe
            String sqlCheck = "SELECT idUsuario FROM usuarios WHERE email = ?";
            psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setString(1, email);
            rs = psCheck.executeQuery();

            if (rs.next()) {
                // --- El email YA EXISTE ---
                errores.add("El email ingresado ya está registrado.");
                enviarErrores(request, response, errores);
            } else {
                // --- El email es NUEVO, podemos registrar ---
                
                // 6. Preparar el INSERT
                // Usamos los nombres de columnas de tu SQL: idRol, password
                String sqlInsert = "INSERT INTO usuarios (idRol, email, password, nombre_completo, esta_activo, fecha_registro) " +
                                 "VALUES (?, ?, ?, ?, ?, NOW())";
                
                psInsert = conn.prepareStatement(sqlInsert);
                
                // NOTA: Asumimos que el idRol de "cliente" es 1.
                // Esto debería estar en los datos iniciales (seeds) de tu BBDD.
                psInsert.setInt(1, 1); 
                psInsert.setString(2, email);
                
                // ¡¡ REQUISITO !! Guardando contraseña en TEXTO PLANO
                psInsert.setString(3, pass1); 
                
                psInsert.setString(4, nombreCompleto);
                psInsert.setBoolean(5, true); // La cuenta se crea activa por defecto

                // 7. Ejecutar el INSERT
                // Nos devuelve el número de filas insertadas (1).
                int filasInsertadas = psInsert.executeUpdate();

                // Se realiza en caso que el INSERT falle.
                if (filasInsertadas > 0) {
                    // ¡Registro exitoso!
                    // Redirigimos al login con un mensaje de éxito.
                    response.sendRedirect("login.jsp?registro=exitoso");
                } else {
                    errores.add("Ocurrió un error inesperado al crear la cuenta.");
                    enviarErrores(request, response, errores);
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace(); 
            errores.add("Error interno: No se encontró el driver de la base deatos.");
            enviarErrores(request, response, errores);
        } catch (SQLException e) {
            e.printStackTrace(); 
            errores.add("Error interno: Problema al conectar con la base de datos.");
            enviarErrores(request, response, errores);
        } finally {
            // 8. Cerrar todas las conexiones de JDBC
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (psCheck != null) psCheck.close();
            } catch (SQLException e) { e.printStackTrace(); }
             try {
                if (psInsert != null) psInsert.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Método de ayuda para reenviar al usuario a registro.jsp con una lista de errores.
     */
    private void enviarErrores(HttpServletRequest request, HttpServletResponse response, List<String> errores)
            throws ServletException, IOException {
        
        // Guardamos la lista de errores para que registro.jsp la muestre
        request.setAttribute("listaErrores", errores);
        
        // Reenviamos la petición (forward). 
        // Esto mantiene los datos que el usuario ya había escrito (en 'request.getParameter')
        // para que el JSP pueda auto-rellenarlos.
        request.getRequestDispatcher("registro.jsp").forward(request, response);
    }
    
}
