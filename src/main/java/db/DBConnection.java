package db;
/**
 * Este paquete 'db' contendrá nuestras clases de ayuda
 * para la base de datos.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase de utilidad para manejar la conexión a la base de datos (JDBC).
 * Centraliza las credenciales y los métodos de conexión y cierre con la base de datos.
 */

public class DBConnection {
    
    // Usamos las credenciales y URL de la base de datos
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fotocopiadora?ServerTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static {
    	// Carga el Driver
        try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor privado.
     * Esto previene que alguien pueda crear una instancia de DBConnection (ej. new DBConnection()).
     * Es una "clase de utilidad", solo usamos sus métodos estáticos.
     */
    private DBConnection() {
        // Constructor vacío y privado
    }

    /**
     * Obtiene una nueva conexión a la base de datos.
     * Quien llame a este método es responsable de CERRAR la conexión.
     * * @return un objeto Connection
     * @throws SQLException si hay un error de SQL
     * @throws ClassNotFoundException si falta el Driver JDBC
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        
        // Obtener y devolver la conexión
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }

    /**
     * Cierra de forma segura un Connection, PreparedStatement y ResultSet.
     * * @param conn La conexión a cerrar
     * @param ps El PreparedStatement a cerrar
     * @param rs El ResultSet a cerrar
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log del error
        }
        
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log del error
        }
        
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log del error
        }
    }
    
    /**
     * Sobrecarga del método close para operaciones que no usan ResultSet
     * (como INSERT, UPDATE, DELETE).
     * * @param conn La conexión a cerrar
     * @param ps El PreparedStatement a cerrar
     */
    public static void close(Connection conn, PreparedStatement ps) {
        // Simplemente llamamos al método principal, pasando null para el ResultSet
        close(conn, ps, null);
    }
}
