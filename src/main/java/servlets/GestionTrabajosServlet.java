package servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Utils;

import java.io.IOException;
import java.util.List;

import dao.TrabajoDAO;
import clases.Trabajo;

@WebServlet("/GestionTrabajosServlet")
public class GestionTrabajosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private TrabajoDAO trabajoDAO;

    public GestionTrabajosServlet() {
        super();
        this.trabajoDAO = new TrabajoDAO();
    }

    /**
     * GET: O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	// 1. Seguridad: Comprueba sesión abierta por un administrador.
    	if (!Utils.esAdmin(request, response)) {
    		return;
    	}

        // 2. Obtener Filtros
        String filtroEstado = request.getParameter("filtroEstado"); // "pendiente", "terminado", "todos"
        String orden = request.getParameter("orden"); // "nuevo", "antiguo", "retiro_urgente"
        
        // Valores por defecto
        if (filtroEstado == null) filtroEstado = "todos";
        if (orden == null) orden = "nuevo"; // fecha solicitud desc

        try {
            // 3. Obtener lista del DAO
            List<Trabajo> listaTrabajos = trabajoDAO.getAllTrabajos(filtroEstado, orden);
            
            // 4. Enviar al JSP
            request.setAttribute("listaTrabajos", listaTrabajos);
            request.setAttribute("filtroEstadoActual", filtroEstado);
            request.setAttribute("ordenActual", orden);
            
            request.getRequestDispatcher("gestionTrabajos.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, volvemos al panel principal
            response.sendRedirect("paginaPrincipalAdmin.jsp");
        }
    }
    /**
     * POST: 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	// Comprueba sesión abierta por un administrador.
    	if (!Utils.esAdmin(request, response)) {
    		return;
    	}
        
        String accion = request.getParameter("accion");
        
        if ("cambiarEstado".equals(accion)) {
            try {
                int idTrabajo = Integer.parseInt(request.getParameter("idTrabajo"));
                String nuevoEstado = request.getParameter("nuevoEstado");
                
                // Validamos que el estado sea válido
                if ("pendiente".equals(nuevoEstado) || "terminado".equals(nuevoEstado) || "retirado".equals(nuevoEstado)) {
                    trabajoDAO.actualizarEstadoTrabajo(idTrabajo, nuevoEstado);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Redirigimos al GET para recargar la tabla
        response.sendRedirect("GestionTrabajosServlet");
    }
}
