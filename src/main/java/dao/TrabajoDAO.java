package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "INSERT INTO trabajos (idCliente, nombre_archivo, nombre_archivo_original, " +
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
            ps.setString(2, trabajo.getNombreArchivo());
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
    
    /**
     * Obtiene todos los trabajos de un cliente específico, con filtros y orden.
     *
     * @param idCliente El ID del cliente del cual se busca el historial.
     * @param filtroEstado El estado por el cual filtrar (ej. "pendiente") o "" para todos.
     * @param orden La columna y dirección por la cual ordenar (ej. "fecha_sol_desc").
     * @return Una lista de objetos Trabajo.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Trabajo> getTrabajosPorCliente(int idCliente, String filtroEstado, String orden) 
            throws SQLException, ClassNotFoundException {
        
        List<Trabajo> listaTrabajos = new ArrayList<>();
        
        // --- 1. Construcción de la consulta SQL ---
        // Base de la consulta. Usamos StringBuilder porque, a diferencia de String, este se puede modificar dinamicamente.
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM trabajos WHERE idCliente = ? "
        );

        // Añadir filtro de estado (si existe)
        if (filtroEstado != null && !filtroEstado.isEmpty() && !filtroEstado.equals("todos")) {
            sql.append(" AND estado = ? ");
        }

        // Ordenamiento de los trabajos (con validación para evitar SQL Injection)
        // Por defecto, ordenamos por fecha de solicitud más nueva.
        String orderBySql = " ORDER BY fecha_solicitud DESC";
        switch (orden) {
            case "fecha_sol_asc":
                orderBySql = " ORDER BY fecha_solicitud ASC";
                break;
            case "fecha_retiro_desc":
                orderBySql = " ORDER BY fecha_retiro_solicitada DESC";
                break;
            case "fecha_retiro_asc":
                orderBySql = " ORDER BY fecha_retiro_solicitada ASC";
                break;
        }
        sql.append(orderBySql);

        // --- 2. Ejecución de la consulta ---
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql.toString());
            
            // Seteamos los parámetros dinámicos
            int paramIndex = 1;
            // La suma se realiza después de enviar el parámetro, por lo que envía 1 al método y luego incrementa la variable.
            ps.setInt(paramIndex++, idCliente);
            
            if (filtroEstado != null && !filtroEstado.isEmpty() && !filtroEstado.equals("todos")) {
                ps.setString(paramIndex++, filtroEstado);
            }

            // Ejecutamos
            rs = ps.executeQuery();

            // 3. Mapear Resultados a Objetos
            while (rs.next()) {
                Trabajo trabajo = new Trabajo();
                trabajo.setIdTrabajo(rs.getInt("idTrabajo"));
                trabajo.setIdCliente(rs.getInt("idCliente"));
                trabajo.setNombreArchivo(rs.getString("nombre_archivo"));
                trabajo.setNombreArchivoOriginal(rs.getString("nombre_archivo_original"));
                trabajo.setNumCopias(rs.getInt("num_copias"));
                trabajo.setCalidad(rs.getString("calidad"));
                trabajo.setFaz(rs.getString("faz"));
                trabajo.setEstado(rs.getString("estado"));
                trabajo.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
                trabajo.setFechaRetiroSolicitada(rs.getTimestamp("fecha_retiro_solicitada"));
                trabajo.setFechaImpresion(rs.getTimestamp("fecha_impresion"));
                trabajo.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
                
                listaTrabajos.add(trabajo);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBConnection.close(conn, ps, rs);
        }

        return listaTrabajos;
    }
    
    /**
     * Obtiene el nombre del archivo físico de un trabajo antes de borrarlo.
     * También verifica la propiedad (idCliente) y el estado (pendiente) por seguridad.
     *
     * @param idTrabajo El ID del trabajo.
     * @param idCliente El ID del cliente que solicita el borrado.
     * @return El String de la nombre_archivo, o null si no se encuentra.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String getNombreArchivoParaBorrar(int idTrabajo, int idCliente) throws SQLException, ClassNotFoundException {
        
        String sql = "SELECT nombre_archivo FROM trabajos WHERE idTrabajo = ? AND idCliente = ? AND estado = 'pendiente'";
        String rutaArchivo = null;
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTrabajo);
            ps.setInt(2, idCliente);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                rutaArchivo = rs.getString("nombre_archivo");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        
        return rutaArchivo;
    }
    
    /**
     * Borra un trabajo específico.
     * Solo permite borrar si el ID del cliente coincide Y el estado es "pendiente".
     *
     * @param idTrabajo El ID del trabajo a borrar.
     * @param idCliente El ID del cliente (para seguridad).
     * @return true si el borrado fue exitoso.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public boolean borrarTrabajo(int idTrabajo, int idCliente) throws SQLException, ClassNotFoundException {
        
        String sql = "DELETE FROM trabajos WHERE idTrabajo = ? AND idCliente = ? AND estado = 'pendiente'";
        
        Connection conn = null;
        PreparedStatement ps = null;
        int filasAfectadas = 0;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, idTrabajo);
            ps.setInt(2, idCliente); // Seguridad
            
            filasAfectadas = ps.executeUpdate();
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBConnection.close(conn, ps, null);
        }

        // Si filasAfectadas es > 0, significa que borró la fila.
        return filasAfectadas > 0;
    }
    
    /**
     * Obtiene un trabajo específico por su ID y el ID del cliente (por seguridad).
     * El trabajo incluye el nombre con el que fue guardado el archivo y el nombre que le puso el usuario.
     * @param idTrabajo El ID del trabajo para obtener sus datos de archivo.
     * @param idCliente El ID del cliente (para seguridad).
     * @return Objeto Trabajo SOLO CON el nombre del archivo original y el nombre del archivo en el servidor.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Trabajo getDatosArchivo(int idTrabajo, int idCliente) throws SQLException, ClassNotFoundException {
        
        String sql = "SELECT nombre_archivo, nombre_archivo_original FROM trabajos WHERE idTrabajo = ? AND idCliente = ?";
        Trabajo trabajo = null;
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTrabajo);
            ps.setInt(2, idCliente);
            
            rs = ps.executeQuery();

            if (rs.next()) {
                trabajo = new Trabajo();
                // Solo llamamos estos dos campos
                trabajo.setNombreArchivo(rs.getString("nombre_archivo"));
                trabajo.setNombreArchivoOriginal(rs.getString("nombre_archivo_original"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBConnection.close(conn, ps, rs);
        }

        return trabajo;
    }
    
    /**
     * Obtiene datos del archivo sin validar el idCliente.
     * ¡SOLO PARA USO DE ADMINISTRADORES!
     * @param idTrabajo El ID del trabajo para obtener sus datos de archivo.
     * @return Objeto Trabajo SOLO CON el nombre del archivo original y el nombre del archivo en el servidor.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Trabajo getDatosArchivoAdmin(int idTrabajo) throws SQLException, ClassNotFoundException {
        
        // SELECT sin filtrar por cliente
        String sql = "SELECT nombre_archivo, nombre_archivo_original FROM trabajos WHERE idTrabajo = ?";
        Trabajo trabajo = null;
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTrabajo); // Solo un parámetro
            
            rs = ps.executeQuery();

            if (rs.next()) {
                trabajo = new Trabajo();
                trabajo.setNombreArchivo(rs.getString("nombre_archivo"));
                trabajo.setNombreArchivoOriginal(rs.getString("nombre_archivo_original"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
        	// Cerramos la conexión con la base de datos.
            DBConnection.close(conn, ps, rs);
        }

        return trabajo;
    }
    
    /**
     * Obtiene TODOS los trabajos de TODOS los clientes.
     * Incluye datos del cliente (nombre, email) mediante JOIN.
     * @param filtroEstado para que SQL filtre los trabajos de acuerdo a su estado.
     * @param orden para que SQL ordene los trabajos cronológicamente ya sea por su fecha de solicitud o de retiro.
     * @return Lista Trabajos.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Trabajo> getAllTrabajos(String filtroEstado, String orden) throws SQLException, ClassNotFoundException {
        
        List<Trabajo> lista = new ArrayList<>();
        
        // Hacemos JOIN con usuarios para saber de quién es el trabajo
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, u.nombre_completo, u.email " +
            "FROM trabajos t " +
            "JOIN usuarios u ON t.idCliente = u.idUsuario " +
            "WHERE 1=1 " // Truco para concatenar ANDs fácilmente
        );

        if (filtroEstado != null && !filtroEstado.isEmpty() && !filtroEstado.equals("todos")) {
            sql.append(" AND t.estado = ? ");
        }

        // Ordenamiento de los trabajos (con validación para evitar SQL Injection)
        String orderBySQL = " ORDER BY t.fecha_solicitud DESC"; // Por defecto: lo más nuevo primero
        switch (orden) {
        case "fecha_sol_asc":
        	orderBySQL = " ORDER BY fecha_solicitud ASC";
            break;
        case "fecha_retiro_desc":
        	orderBySQL = " ORDER BY fecha_retiro_solicitada DESC";
            break;
        case "fecha_retiro_asc":
        	orderBySQL = " ORDER BY fecha_retiro_solicitada ASC";
            break;
        }
        sql.append(orderBySQL);

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql.toString());
            
            // Seteamos parámetro de filtro si existe
            if (filtroEstado != null && !filtroEstado.isEmpty() && !filtroEstado.equals("todos")) {
                ps.setString(1, filtroEstado);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                Trabajo t = new Trabajo();
                // Datos del Trabajo
                t.setIdTrabajo(rs.getInt("idTrabajo"));
                t.setIdCliente(rs.getInt("idCliente"));
                t.setNombreArchivo(rs.getString("nombre_archivo"));
                t.setNombreArchivoOriginal(rs.getString("nombre_archivo_original"));
                t.setNumCopias(rs.getInt("num_copias"));
                t.setCalidad(rs.getString("calidad"));
                t.setFaz(rs.getString("faz"));
                t.setEstado(rs.getString("estado"));
                t.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
                t.setFechaRetiroSolicitada(rs.getTimestamp("fecha_retiro_solicitada"));
                t.setFechaImpresion(rs.getTimestamp("fecha_impresion"));
                t.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
                
                // Datos del Cliente (Extra)
                t.setNombreCliente(rs.getString("nombre_completo"));
                t.setEmailCliente(rs.getString("email"));
                
                lista.add(t);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        
        return lista;
    }

    /**
     * Actualiza el estado de un trabajo y establece la fecha correspondiente.
     * @param idTrabajo para que encuentre el trabajo en la BBDD.
     * @param nuevoEstado que determina el nuevo estado del trabajo.
     * @return true si se realizó el cambio o false si no se modificó la BBDD.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public boolean actualizarEstadoTrabajo(int idTrabajo, String nuevoEstado) throws SQLException, ClassNotFoundException {
        
        StringBuilder sql = new StringBuilder("UPDATE trabajos SET estado = ? ");
        
        // Si pasa a 'terminado', actualizamos fecha_impresion
        if ("terminado".equals(nuevoEstado)) {
            sql.append(", fecha_impresion = NOW() ");
        }
        // Si pasa a 'retirado', actualizamos fecha_entrega
        else if ("retirado".equals(nuevoEstado)) {
            sql.append(", fecha_entrega = NOW() ");
        }
        
        sql.append("WHERE idTrabajo = ?");
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idTrabajo);
            
            // Este método cuenta el número de filas que fueron modificadas.
            // Por esto, si se modificó una fila, devuelve true. Si modificó cero filas, devuelve false.
            return ps.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBConnection.close(conn, ps);
        }
    }
    /**
     * Revierte el estado de un trabajo y borra la fecha establecida en actualizarEstadoTrabajo.
     * Sirve en caso que el administrador haya cambiado el estado de un trabajo por error.
     * @param idTrabajo para que encuentre el trabajo en la BBDD.
     * @param nuevoEstado que determina el nuevo estado del trabajo.
     * @return true si se realizó el cambio o false si no se modificó la BBDD.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public boolean revertirEstadoTrabajo(int idTrabajo, String nuevoEstado) throws SQLException, ClassNotFoundException {
        
        StringBuilder sql = new StringBuilder("UPDATE trabajos SET estado = ? ");
        
        // Si pasa a 'pendiente', eliminamos la fecha de impresión y entrega
        if ("pendiente".equals(nuevoEstado)) {
            sql.append(", fecha_impresion = NULL, fecha_entrega = NULL ");
        }
        // Si pasa a 'terminado', eliminamos la fecha de entrega
        else if ("terminado".equals(nuevoEstado)) {
            sql.append(", fecha_entrega = NULL ");
        }
        
        sql.append("WHERE idTrabajo = ?");
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idTrabajo);
            
            // Este método cuenta el número de filas que fueron modificadas.
            // Por esto, si se modificó una fila, devuelve true. Si modificó cero filas, devuelve false.
            return ps.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBConnection.close(conn, ps);
        }
    }
}