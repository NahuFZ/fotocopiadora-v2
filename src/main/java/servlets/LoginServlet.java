package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//Imports de JDBC (Java Database Connectivity)
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(description = "Servlet para el inicio de sesión", urlPatterns = { "/LoginServlet" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	// --- Configuración de la Base de Datos ---
    // ¡¡ CAMBIA ESTO !! Pon tu usuario y contraseña de MySQL.
    private String jdbcURL = "jdbc:mysql://localhost:3306/fotocopiadora?ServerTimezone=UTC";
    private String jdbcUsername = "root";
    private String jdbcPassword = ""; // Escribe tu contraseña aquí si tienes una
    

    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
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

        // Variables de JDBC
        // conn realiza la conexión con la DB.
        Connection conn = null;
        // ps realiza la consulta para recuperar los datos.
        PreparedStatement ps = null;
        // rs recibe el resultado de la consulta.
        ResultSet rs = null;

        try {
            // 2. Conectar a la base de datos. Primero cargamos en memoria el archivo .jar
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecemos la conexipon con la base de datos.
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            // 3. Preparar la consulta SQL
            // Es una buena práctica traer el rol del usuario en la misma consulta
            // Usamos un JOIN con la tabla 'roles'.
            // Usamos los nombres de columnas de tu SQL: idRol, idUsuario, etc.
            // En lugar de "pegar" el email del usuario en el string de SQL 
            // (lo cual es vulnerable a Inyección SQL), creamos una "plantilla" con un ?.
            String sql = "SELECT u.idUsuario, u.password, u.nombre_completo, u.esta_activo, r.nombre_rol " +
                         "FROM usuarios u " +
                         "INNER JOIN roles r ON u.idRol = r.idRol " +
                         "WHERE u.email = ?";
            
            ps = conn.prepareStatement(sql);
            // Esta linea le dice a la base de datos: "Toma el texto de la variable email y colócalo de forma segura en el primer ?". 
            // La base de datos se encarga de "limpiar" el texto para que no sea malicioso.
            ps.setString(1, email);

            // 4. Ejecutar la consulta
            rs = ps.executeQuery();

            // 5. Procesar el resultado
            // rs es un puntero que empieza antes de la primera fila de resultados
            // Si encontro el usuario, devuelve true. Si no lo encontró, devuelve false.
            if (rs.next()) {
                // --- Usuario ENCONTRADO ---
                
                // Obtenemos los datos de la BBDD
                String passwordGuardado = rs.getString("password");
                boolean estaActivo = rs.getBoolean("esta_activo");
                
                // 5a. Verificar la contraseña
                if (password.equals(passwordGuardado)) {
                    // --- Contraseña CORRECTA ---
                    
                    // 5b. Verificar si la cuenta está activa
                    if (estaActivo) {
                        // --- LOGIN EXITOSO ---
                        
                        // Creamos una sesión para el usuario
                        HttpSession session = request.getSession();
                        
                        // Guardamos los datos del usuario en la sesión
                        session.setAttribute("idUsuario", rs.getInt("idUsuario"));
                        session.setAttribute("nombreCompleto", rs.getString("nombre_completo"));
                        session.setAttribute("nombreRol", rs.getString("nombre_rol"));
                        
                        // 5c. Redirigir según el rol
                        String nombreRol = rs.getString("nombre_rol");
                        if ("admin".equals(nombreRol)) {
                            response.sendRedirect("paginaPrincipalAdmin.jsp");
                        } else {
                            response.sendRedirect("paginaPrincipalCliente.jsp");
                        }
                        
                    } else {
                        // Error: Cuenta inactiva
                        enviarError(request, response, "Su cuenta está desactivada. Contactese al administrador.");
                    }
                    
                } else {
                    // Error: Contraseña incorrecta
                    enviarError(request, response, "Email o contraseña incorrectos.");
                }
                
            } else {
                // Error: Usuario no encontrado
                enviarError(request, response, "Email o contraseña incorrectos.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Muestra el error en la consola de Tomcat
            enviarError(request, response, "Error interno: No se encontró el driver de la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace(); // Muestra el error en la consola de Tomcat
            enviarError(request, response, "Error interno: Problema al conectar con la base de datos.");
        } finally {
            // 6. Cerrar todas las conexiones de JDBC en orden inverso
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
	}
	  /**
     * Método de ayuda para reenviar al usuario a login.jsp con un mensaje de error.
     */
    private void enviarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
