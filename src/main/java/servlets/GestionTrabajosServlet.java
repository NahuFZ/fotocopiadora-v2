package servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.TrabajoDAO;
import clases.Trabajo;

@WebServlet("/GestionTrabajosServlet")
public class GestionTrabajosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private TrabajoDAO trabajoDAO;
    
    private static final String JSP = "gestionTrabajos.jsp";
    
    public GestionTrabajosServlet() {
        super();
        this.trabajoDAO = new TrabajoDAO();
    }

    /**
     * GET: Se busca la lista de trabajos de acuerdo a los filtros aplicados.
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
        if (orden == null) orden = "fecha_sol_desc"; // Valor por defecto

        try {
            // 3. Obtener lista del DAO
            List<Trabajo> listaTrabajos = trabajoDAO.getAllTrabajos(filtroEstado, orden);
            
            // 4. Enviar al JSP
            request.setAttribute("listaTrabajos", listaTrabajos);
            request.setAttribute("filtroEstadoActual", filtroEstado);
            request.setAttribute("ordenActual", orden);
            
            request.getRequestDispatcher("gestionTrabajos.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            String mensaje = "Fallo al conectarse a la base de datos.";
            Utils.enviarError(request, response, mensaje, JSP);
        }
        catch (ClassNotFoundException e) {
        	e.printStackTrace();
            String mensaje = "No se encuentra el driver JDBC.";
            Utils.enviarError(request, response, mensaje, JSP);
        }
        catch (Exception e) {
            e.printStackTrace();
            // En caso de error, volvemos al panel principal
            String mensaje = "Fallo interno del servidor.";
            Utils.enviarError(request, response, mensaje, JSP);
        }
    }
    /**
     * POST: Se cambia el estado del trabajo de "pendiente" a "terminado" y de "terminado" a "entregado".
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
                
            } catch (NumberFormatException e) {
                e.printStackTrace();
                String mensaje = "No se pudo pasar el id del trabajo a Int.";
                Utils.enviarError(request, response, mensaje, JSP);
            }
            catch (SQLException e) {
                e.printStackTrace();
                String mensaje = "Fallo al conectarse a la base de datos.";
                Utils.enviarError(request, response, mensaje, JSP);
            }
            catch (ClassNotFoundException e) {
            	e.printStackTrace();
                String mensaje = "No se encuentra el driver JDBC.";
                Utils.enviarError(request, response, mensaje, JSP);
            }
        }
        
        // Redirigimos al GET para recargar la tabla
        response.sendRedirect("GestionTrabajosServlet");
    }
}
