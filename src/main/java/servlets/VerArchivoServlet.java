package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // Error 401
            return;
        }
        
        int idCliente = (Integer) session.getAttribute("idUsuario");
        // Nota: Cuando hagamos la parte de Admin, aquí deberemos permitir
        // también si el rol es "admin". Por ahora solo cliente.
        
        // 2. Obtener ID del trabajo en String
        String idTrabajoStr = request.getParameter("id");
        if (idTrabajoStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST); // Error 400
            return;
        }
        
        try {
            int idTrabajo = Integer.parseInt(idTrabajoStr);
            
            // 3. Buscar el trabajo en la BBDD
            Trabajo trabajo = trabajoDAO.getDatosArchivo(idTrabajo, idCliente);
            
            if (trabajo == null || trabajo.getRutaArchivo() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND); // Archivo no encontrado en BBDD
                return;
            }
            
            // 4. Localizar el archivo físico
            File archivo = new File(trabajo.getRutaArchivo());
            
            if (!archivo.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND); // Archivo no encontrado en Disco
                return;
            }
            
            // 5. Configurar cabeceras HTTP para la respuesta
            
            // Adivinar el tipo MIME (pdf, jpg, png) basado en el nombre
            String mimeType = getServletContext().getMimeType(archivo.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // Tipo genérico binario
            }
            
            response.setContentType(mimeType);
            
            // Las dos opciones de Content-Disposition para mostrar el archivo son:
            // "inline": Trata de mostrarlo en el navegador.
            // "attachment": Lo fuerza a descargar.
            response.setHeader("Content-Disposition", "inline; filename=\"" + trabajo.getNombreArchivoOriginal() + "\"");
            
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
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
