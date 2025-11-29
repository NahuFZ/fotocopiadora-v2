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
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

import dao.TrabajoDAO;
import clases.Trabajo;

/**
 * Servlet que controla la página de historial de pedidos del cliente.
 * Maneja GET para mostrar la lista y POST para borrar trabajos.
 */
@WebServlet("/HistorialPedidosServlet")
public class HistorialPedidosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private TrabajoDAO trabajoDAO;

    public HistorialPedidosServlet() {
        super();
        this.trabajoDAO = new TrabajoDAO();
    }
    /**
     * Maneja el GET: Muestra la lista de trabajos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	// --- 1. Comprueba sesión abierta de cliente ---
    	if (!Utils.esCliente(request, response)) {
    		return;
    	}
    	
    	// Con false evitamos crear una sesión si esta no existe.
        HttpSession session = request.getSession(false);
        
        int idCliente = (Integer) session.getAttribute("idUsuario");
        
        // --- 2. Obtener Filtros de la URL (si existen) ---
        String filtroEstado = request.getParameter("filtroEstado");
        if (filtroEstado == null) {
            filtroEstado = "todos"; // Valor por defecto
        }
        
        String orden = request.getParameter("orden");
        if (orden == null) {
            orden = "fecha_sol_desc"; // Valor por defecto
        }

        // --- 3. Llamar al DAO para obtener la lista ---
        try {
            List<Trabajo> listaTrabajos = trabajoDAO.getTrabajosPorCliente(idCliente, filtroEstado, orden);
            
            // --- 4. Poner la lista en el request ---
            request.setAttribute("listaTrabajos", listaTrabajos);
            
            // Guardamos los filtros seleccionados para que el JSP pueda recordarlos
            request.setAttribute("filtroEstadoActual", filtroEstado);
            request.setAttribute("ordenActual", orden);
            
            // --- 5. Reenviar al JSP ---
            request.getRequestDispatcher("historialPedidos.jsp").forward(request, response);
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Si hay un error de BBDD, enviamos al JSP con un mensaje
            request.setAttribute("errorHistorial", "Error al cargar el historial. Intente más tarde.");
            request.getRequestDispatcher("historialPedidos.jsp").forward(request, response);
        }
    }

    /**
     * Maneja el POST: Borra un trabajo.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	// --- 1. Comprueba sesión abierta de cliente ---
    	if (!Utils.esCliente(request, response)) {
    		return;
    	}
    	
    	// Tomamos la sesión existente sin crear una nueva en caso que exista.
        HttpSession session = request.getSession(false);
        
        int idCliente = (Integer) session.getAttribute("idUsuario");
        String action = request.getParameter("action");
        
        // --- 2. Lógica de Borrado ---
        if ("borrar".equals(action)) {

            String nombreArchivoGuardado = null;
            
            try {
            	int idTrabajo = Integer.parseInt(request.getParameter("idTrabajo"));

                // PASO 1: Obtener la ruta del archivo ANTES de borrar el registro
                nombreArchivoGuardado = trabajoDAO.getNombreArchivoParaBorrar(idTrabajo, idCliente);
                
                // PASO 2: Borrar el registro de la BBDD
                boolean borradoExitoso = trabajoDAO.borrarTrabajo(idTrabajo, idCliente);

                // PASO 3: Si la BBDD se borró Y encontramos una ruta de archivo...
                if (borradoExitoso && nombreArchivoGuardado != null && !nombreArchivoGuardado.isEmpty()) {
                    
                    // PASO 4: Borrar el archivo físico
                    try {
                        File archivo = new File(AppConfig.DIRECTORIO_ARCHIVOS, nombreArchivoGuardado);
                        if (archivo.exists()) {
                            Files.delete(archivo.toPath());
                            System.out.println("Archivo físico borrado exitosamente: " + nombreArchivoGuardado);
                        } else {
                             System.err.println("Advertencia: Se borró el registro de BBDD pero el archivo físico no se encontró en: " + nombreArchivoGuardado);
                        }
                    } catch (IOException e) {
                        // Error Crítico: La BBDD se borró, pero el archivo no.
                        // Esto debe registrarse.
                        System.err.println("ERROR DE ROLLBACK: No se pudo borrar el archivo físico: " + nombreArchivoGuardado);
                        e.printStackTrace();
                        // (Opcional: Guardar un mensaje en sesión para el admin.)
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Error si el idTrabajo no es un número
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace(); // Error de BBDD
            }
        }
        // --- 3. Redirigir de vuelta al GET ---
        // Después de borrar (o fallar), siempre redirigimos de vuelta
        // al método GET para que recargue la lista actualizada.
        response.sendRedirect("HistorialPedidosServlet");
    }
}
