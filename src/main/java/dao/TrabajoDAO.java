package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import db.DBConnection;
import clases.Trabajo;

/**
 * DAO para manejar todas las operaciones SQL de la tabla 'trabajos'.
 */
public class TrabajoDAO {

    /**
     * Inserta un nuevo trabajo (pedido) en la base de datos.
     *
     * @param trabajo El objeto Trabajo (JavaBean) con todos los datos a insertar.
     * @return true si el INSERT fue exitoso, false en caso contrario.
     * @throws SQLException Si hay un error de SQL.
     * @throws ClassNotFoundException Si falta el driver JDBC.
     */
    public boolean crearTrabajo(Trabajo trabajo) throws SQLException, ClassNotFoundException {
        
        // El SQL omite idTrabajo (AUTO_INCREMENT), estado (DEFAULT 'pendiente'),
        // y fecha_solicitud (DEFAULT current_timestamp()).
        String sql = "INSERT INTO trabajos (idCliente, ruta_archivo, nombre_archivo_original, " +
                     "num_copias, calidad, faz, fecha_retiro_solicitada) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        int filasInsertadas = 0;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            
            // Seteamos los parámetros del PreparedStatement
            ps.setInt(1, trabajo.getIdCliente());
            ps.setString(2, trabajo.getRutaArchivo());
            ps.setString(3, trabajo.getNombreArchivoOriginal());
            ps.setInt(4, trabajo.getNumCopias());
            ps.setString(5, trabajo.getCalidad());
            ps.setString(6, trabajo.getFaz());
            ps.setTimestamp(7, trabajo.getFechaRetiroSolicitada()); // El bean guarda un Timestamp
            
            // Ejecutamos el INSERT
            filasInsertadas = ps.executeUpdate();
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e; // Relanzamos la excepción para que el Servlet la maneje
        } finally {
            // Cerramos la conexión (solo ps y conn, no hay ResultSet)
            DBConnection.close(conn, ps);
        }

        // Devolvemos true si se insertó exactamente 1 fila
        return filasInsertadas > 0;
    }
    
    // Aquí, en el futuro, irán otros métodos como:
    // public List<Trabajo> getTrabajosPorCliente(int idCliente) { ... }
    // public List<Trabajo> getAllTrabajos() { ... }
    // public boolean cambiarEstado(int idTrabajo, String nuevoEstado) { ... }
}