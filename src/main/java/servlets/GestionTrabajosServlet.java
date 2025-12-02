package servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    	String modError = "Tabla trabajos: "; // Identifica al módulo para el mensaje de error.
        
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
            String mensaje = modError + "Fallo al conectarse a la base de datos.";
            Utils.enviarError(request, response, mensaje, JSP);
        }
        catch (ClassNotFoundException e) {
        	e.printStackTrace();
            String mensaje = modError + "No se encuentra el driver JDBC.";
            Utils.enviarError(request, response, mensaje, JSP);
        }
        catch (Exception e) {
            e.printStackTrace();
            // En caso de error, volvemos al panel principal
            String mensaje = modError + "Fallo interno del servidor.";
            Utils.enviarError(request, response, mensaje, JSP);
        }
    }
    /**
     * POST: se actualiza o se revierte el estado de los trabajos.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	// Comprueba sesión abierta por un administrador.
    	if (!Utils.esAdmin(request, response)) {
    		return;
    	}
        
        String accion = request.getParameter("accion");
        String nuevoEstado = request.getParameter("nuevoEstado");
        
        // --- RECUPERAR EL FILTRO Y ORDEN QUE ELIGIÓ EL USUARIO ---
        String filtroEstado = request.getParameter("filtroEstadoActual");
        String orden = request.getParameter("ordenActual");
        // Construimos la URL para volver al GET con los mismos parámetros.
        // Usamos URLEncoder para evitar problemas con espacios o caracteres raros.
        // Solo se utiliza en caso que todo salga bien.
        String redirectURL = "GestionTrabajosServlet?filtroEstado=" + 
                             URLEncoder.encode(filtroEstado, StandardCharsets.UTF_8) + 
                             "&orden=" + 
                             URLEncoder.encode(orden, StandardCharsets.UTF_8);
        String modError = ""; // Identifica al módulo que falló para el mensaje de error.
        try {
        	// Recuperamos el trabajo.
        	int idTrabajo = Integer.parseInt(request.getParameter("idTrabajo"));
        	
        	// OPCIÓN 1: ACTUALIZAR ESTADO
            // Cambio de estado del trabajo de "pendiente" a "terminado" o de "terminado" a "retirado"
        	if ("cambiarEstado".equals(accion)) {
            	modError = "Actualizar trabajo: ";
                // Validamos que el estado coincida con las opciones disponibles
                if ("terminado".equals(nuevoEstado) || "retirado".equals(nuevoEstado)) {
                	
                	// realizamos el cambio y comprobamos que se haya realizado 
                    if (!trabajoDAO.actualizarEstadoTrabajo(idTrabajo, nuevoEstado)){
                    	// Escenario de fallo.
                    	String mensaje = "Fallo al cambiar el estado.";
                    	Utils.enviarError(request, response, mensaje, JSP);
                    }
                }
            }
        	
        	// OPCIÓN 2: REVERTIR ESTADO
            // Cambio de estado del trabajo de "pendiente" a "terminado" o de "terminado" a "retirado"
            else if ("revertirEstado".equals(accion)){
            	modError = "Revertir trabajo: "; // Identifica al módulo para el mensaje de error.
                // Validamos que el estado coincida con las opciones disponibles
                if ("pendiente".equals(nuevoEstado) || "terminado".equals(nuevoEstado)) {
                	
                	// realizamos el cambio y comprobamos que se haya realizado 
                    if (!trabajoDAO.revertirEstadoTrabajo(idTrabajo, nuevoEstado)) {
                    	// Escenario de fallo.
                    	String mensaje = modError + "Fallo al cambiar el estado.";
                    	Utils.enviarError(request, response, mensaje, JSP);                        	
                    }
                }
            }
        	
        } catch (NumberFormatException e) {
            e.printStackTrace();
            String mensaje = modError + "No se pudo pasar el id del trabajo a Int.";
            Utils.enviarError(request, response, mensaje, JSP);
            return;
        }
        catch (SQLException e) {
	        e.printStackTrace();
	        String mensaje = modError + "Fallo al conectarse a la base de datos.";
	        Utils.enviarError(request, response, mensaje, JSP);
        }
        catch (ClassNotFoundException e) {
        	e.printStackTrace();
            String mensaje = modError + "No se encuentra el driver JDBC.";
            Utils.enviarError(request, response, mensaje, JSP);
        }
        
        // Enviamos el filtro y el orden de nuevo por este Servlet por GET.
    	response.sendRedirect(redirectURL);
    }
}
