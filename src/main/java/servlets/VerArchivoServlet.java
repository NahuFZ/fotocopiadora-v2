package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.AppConfig;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import dao.TrabajoDAO;
import clases.Trabajo;

/**
 * Servlet que lee un archivo del disco local (fuera del servidor web)
 * y lo envía al navegador para su previsualización.
 */
@WebServlet("/VerArchivoServlet")
public class VerArchivoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private TrabajoDAO trabajoDAO;

    public VerArchivoServlet() {
        super();
        this.trabajoDAO = new TrabajoDAO();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Seguridad: Verificar sesión
		// Si la sesión no existe, lo enviamos al login
        HttpSession session = request.getSession(false);
        if (!Utils.comprobarSesion(session)) {
        	String mensaje = "La sesión ha caducado";
        	System.err.print("Error: La sesión ha caducado" + System.lineSeparator());
        	Utils.enviarError(request, response, mensaje, "errorArchivo.jsp");
            return;
        }
        
        int idUsuario = (Integer) session.getAttribute("idUsuario");
        String nombreRol = (String) session.getAttribute("nombreRol");
        
        // 2. Obtener ID del trabajo en String
        String idTrabajoStr = request.getParameter("id");
        
        // VALIDACIÓN: Si el id del trabajo no existe.
        if (idTrabajoStr == null) {
        	String mensaje = "No se ha podido obtener el ID del pedido";
        	Utils.enviarError(request, response, mensaje, "errorArchivo.jsp");
            return;
        }
        
        try {
            int idTrabajo = Integer.parseInt(idTrabajoStr);
            Trabajo trabajo = null;
            
            // 3. Buscar el trabajo en la BBDD
            if ("admin".equals(nombreRol)) {
                // Si es ADMIN, usa el método sin restricción de cliente
                trabajo = trabajoDAO.getDatosArchivoAdmin(idTrabajo);
            } else {
                // Si es CLIENTE, usa el método seguro que verifica propiedad
                trabajo = trabajoDAO.getDatosArchivo(idTrabajo, idUsuario);
            }
            
            // Tomamos el nombre con el que fue guardado el archivo.
            String nombreArchivoGuardado = trabajo.getNombreArchivo();
            
            // VALIDACIÓN: Si el archivo no se encuentra en BBDD o el cliente no tiene permiso.
            if (trabajo == null || nombreArchivoGuardado == null) {
            	String mensaje = "El archivo solicitado no existe en la base de datos o no tienes permiso para verlo.";
            	Utils.enviarError(request, response, mensaje, "errorArchivo.jsp");
                return;
            }
            
            // 4. Localizar el archivo físico
            // Combinamos la constante global + el nombre que vino de la BBDD
            File archivo = new File(AppConfig.DIRECTORIO_ARCHIVOS, nombreArchivoGuardado);
            
            // VALIDACIÓN: Si el archivo físico no está
            if (!archivo.exists()) {
            	String mensaje = "El archivo físico no se encuentra en el servidor (Ruta no válida).";
            	Utils.enviarError(request, response, mensaje, "errorArchivo.jsp");
                return;
            }
            
            // 5. Configurar cabeceras HTTP para la respuesta
            
            // Adivinar el tipo MIME (pdf, jpg, png) basado en el nombre
            String mimeType = getServletContext().getMimeType(archivo.getName());
            if (mimeType == null) {
            	String mensaje = "El tipo de archivo no es compatible.";
            	Utils.enviarError(request, response, mensaje, "errorArchivo.jsp");
                return;
            }
            
            response.setContentType(mimeType);
            
            // Las dos opciones de Content-Disposition para mostrar el archivo son:
            // "inline": Trata de mostrarlo en el navegador.
            // "attachment": Lo fuerza a descargar.
            response.setHeader("Content-Disposition",  request.getParameter("tipo") 
            		+ "; filename=\"" + trabajo.getNombreArchivoOriginal() + "\"");
            
            // Le decimos al navegador cuánto pesa el archivo
            response.setContentLength((int) archivo.length());
            
            // 6. Enviar el archivo (Streaming)
            try (FileInputStream in = new FileInputStream(archivo);
                 OutputStream out = response.getOutputStream()) {
                
                // Buffer de 4KB para copiar datos
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
        } 
        // VERIFICACIÓN: error con SQL
        catch (SQLException e) {
            e.printStackTrace();
            // VERIFICACIÓN: 
            if (!response.isCommitted()) {
                Utils.enviarError(request, response, "Error al conectar a la base de datos.", "errorArchivo.jsp");
            }
        }
        // VERIFICACIÓN: No se encontró el driver JDBC
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                Utils.enviarError(request, response, "No se pudo encontrar el driver JDBC", "errorArchivo.jsp");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            // VERIFICACIÓN: Si la respuesta se comprometió (como por ejemplo, que haya ocurrido un error mientras se 
            // cargaban los archivos), no se envía muestra la pantalla de error al usuario para que no ocurra un segundo error
            // como IllegalStateException.
            if (!response.isCommitted()) {
                Utils.enviarError(request, response, "Error interno del servidor al procesar el archivo.", "errorArchivo.jsp");
            }
        }
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
