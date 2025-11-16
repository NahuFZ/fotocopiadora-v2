/**
 * Este paquete 'dao' contendrá nuestras clases de Acceso a Datos.
 */
package dao;

// Imports de JDBC
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Imports de clases de nuestro proyecto
import db.DBConnection;
import exceptions.AuthException;
import clases.Usuario;

/**
 * UsuarioDAO (Objeto de Acceso a Datos para Usuarios).
 * Esta clase encapsula toda la lógica de base de datos (SQL)
 * relacionada con la tabla 'usuarios'.
 * Es el único que habla con la BBDD sobre usuarios.
 */
public class UsuarioDAO {

    /**
     * Valida las credenciales de un usuario contra la base de datos.
     * (Versión sin Hashing, como solicitó el profesor).
     *
     * @param email El email ingresado por el usuario.
     * @param password El password en texto plano ingresado.
     * @return Un objeto Usuario si el login es exitoso, o null si falla.
     * @throws AuthException Si el login falla (email no existe, pass incorrecta, etc.)
     */
    public Usuario validarLogin(String email, String password) throws AuthException {
        
        // El SQL une usuarios y roles para obtener el nombre del rol.
        String sql = "SELECT u.idUsuario, u.nombre_completo, u.email, u.password, u.esta_activo, r.nombre_rol " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.idRol = r.idRol " +
                     "WHERE u.email = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1. Obtener conexión
            conn = DBConnection.getConnection();
            
            // 2. Preparar la consulta
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            
            // 3. Ejecutar
            rs = ps.executeQuery();

            // 4. Procesar el resultado
            // Si rs.next() es true, significa que se encontró una fila
            // que coincide con ESE email Y ESA contraseña.
            if (rs.next()) {
                // Si encontramos un usuario con ese email, ahora comparamos la contraseña
                
                String passwordGuardada = rs.getString("password");
                boolean estaActivo = rs.getBoolean("esta_activo");

                // Comparamos el password de la BBDD con el que vino del formulario
                if (password.equals(passwordGuardada)) {
                    
                    // Contraseña correcta. Verificamos si está activo.
                    if (estaActivo) {
                        // ¡Login exitoso! Creamos el objeto Usuario
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("idUsuario"));
                        usuario.setNombreCompleto(rs.getString("nombre_completo"));
                        usuario.setEmail(rs.getString("email"));
                        usuario.setNombreRol(rs.getString("nombre_rol"));
                        usuario.setEstaActivo(estaActivo);
                        // No guardamos el password en el objeto de sesión
                        
                        return usuario; // <--- ÚNICO CAMINO DE ÉXITO
                    } else {
                        // Usuario existe, password correcto, pero cuenta inactiva
                        throw new AuthException("Su cuenta está desactivada. Contacte al administrador."); // <--- LANZAMOS ERROR 1
                    }
                } else {
                    // El email existe, pero la contraseña es incorrecta
                    throw new AuthException("La contraseña es incorrecta."); // <--- LANZAMOS ERROR 2
                }
            } else {
                // Si rs.next() es false, el email no se pudo encontrar
                throw new AuthException("El email no se pudo encontrar."); // <--- LANZAMOS ERROR 3
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Envolvemos el error de BBDD en nuestra excepción
            throw new AuthException("Error al conectar con la base de datos."); // <--- LANZAMOS ERROR 4
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Envolvemos el error del Driver en nuestra excepción
            throw new AuthException("Error interno: No se encontró el driver de la BBDD."); // <--- LANZAMOS ERROR 5
        } finally {
            // 5. Cerrar recursos
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Verifica si un email ya existe en la base de datos.
     *
     * @param email El email a verificar.
     * @return true si el email ya existe, false en caso contrario.
     * @throws SQLException Si ocurre un error de BBDD.
     * @throws ClassNotFoundException Si no se encuentra el driver.
     */
    public boolean emailExiste(String email) throws SQLException, ClassNotFoundException {
        String sql = "SELECT idUsuario FROM usuarios WHERE email = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            
            // Si rs.next() es true, significa que encontró una fila
            if (rs.next()) {
                existe = true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e; // Relanzamos la excepción para que el Servlet la maneje
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        
        return existe;
    }

    /**
     * Crea un nuevo usuario de tipo "cliente".
     *
     * @param nombreCompleto El nombre del usuario.
     * @param email El email del usuario.
     * @param password El password en texto plano.
     * @return true si el INSERT fue exitoso, false en caso contrario.
     * @throws SQLException Si ocurre un error de BBDD.
     * @throws ClassNotFoundException Si no se encuentra el driver.
     */
    public boolean crearCliente(String nombreCompleto, String email, String password) throws SQLException, ClassNotFoundException {
        // Asumimos que el idRol de "cliente" es 1.
        // (Sería mejor tenerlo en una constante o buscarlo, pero para este proyecto está bien)
        String sql = "INSERT INTO usuarios (idRol, email, password, nombre_completo, esta_activo, fecha_registro) " +
                     "VALUES (1, ?, ?, ?, 1, NOW())";
        
        Connection conn = null;
        PreparedStatement ps = null;
        int filasInsertadas = 0;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, email);
            ps.setString(2, password); // Guardando en texto plano
            ps.setString(3, nombreCompleto);
            
            filasInsertadas = ps.executeUpdate();
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e; // Relanzamos la excepción para que el Servlet la maneje
        } finally {
            // Nótese que aquí cerramos sin ResultSet
            DBConnection.close(conn, ps, null);
        }

        // Si filasInsertadas > 0, el INSERT fue exitoso
        return filasInsertadas > 0;
    }
    
    // Aquí se podrían agregar más métodos en el futuro:
    // - public Usuario obtenerUsuarioPorId(int idUsuario) { ... }
    // - public List<Usuario> obtenerTodosLosUsuarios() { ... }
    // - public boolean cambiarEstadoCuenta(int idUsuario, boolean estaActivo) { ... }
    // - public boolean cambiarRolUsuario(int idUsuario, int idRol) { ... }
}