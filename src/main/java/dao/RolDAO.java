package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import clases.Rol;

/**
 * RolDAO (Objeto de Acceso a Datos para Roles).
 * Maneja todas las operaciones SQL para la tabla 'roles'.
 */
public class RolDAO {

    /**
     * Obtiene una lista de todos los roles de la base de datos.
     * Útil para rellenar menús desplegables (selects) en el frontend.
     *
     * @return Una lista de objetos Rol.
     * @throws SQLException Si hay un error de SQL.
     * @throws ClassNotFoundException Si falta el driver JDBC.
     */
    public List<Rol> getAllRoles() throws SQLException, ClassNotFoundException {
        
        List<Rol> listaRoles = new ArrayList<>();
        
        // Ordenamos por nombre_rol para que el desplegable aparezca ordenado
        String sql = "SELECT idRol, nombre_rol FROM roles ORDER BY nombre_rol";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1. Obtener conexión
            conn = DBConnection.getConnection();
            
            // 2. Preparar y ejecutar la consulta
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            // 3. Procesar los resultados
            while (rs.next()) {
                // Creamos un objeto Rol por cada fila
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("idRol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                
                // Añadimos el objeto a la lista
                listaRoles.add(rol);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Relanzamos la excepción para que el Servlet la maneje
            throw e; 
        } finally {
            // 4. Cerrar recursos
            DBConnection.close(conn, ps, rs);
        }
        
        return listaRoles;
    }
}
