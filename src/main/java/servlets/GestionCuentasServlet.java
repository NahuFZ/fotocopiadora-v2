package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.Utils;

import java.io.IOException;
import java.util.List;

import dao.RolDAO;
import dao.UsuarioDAO;
import clases.Rol;
import clases.Usuario;

/**
 * Servlet implementation class GestionCuentasServlet
 */
@WebServlet("/GestionCuentasServlet")
public class GestionCuentasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;

    public GestionCuentasServlet() {
        super();
        this.usuarioDAO = new UsuarioDAO();
        this.rolDAO = new RolDAO();
    }

    // --- GET: Mostrar la lista ---
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Seguridad: Comprueba sesión abierta por un administrador.
    	if (!Utils.esAdmin(request, response)) {
    		return;
    	}
        
        try {
            // 2. Obtener los usuarios y los roles existentes.
            List<Usuario> listaUsuarios = usuarioDAO.obtenerTodosLosUsuarios();
            List<Rol> listaRoles = rolDAO.getAllRoles(); // Para el <select>
            
            // 3. Guardar en request y enviar al JSP
            request.setAttribute("listaUsuarios", listaUsuarios);
            request.setAttribute("listaRoles", listaRoles);
            
            request.getRequestDispatcher("gestionCuentas.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            // En una app real, mostraríamos un error mejor
            response.sendRedirect("paginaPrincipalAdmin.jsp");
        }
    }

    // --- POST: Procesar cambios ---
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

    	// 1. Seguridad: Comprueba sesión abierta por un administrador.
    	if (!Utils.esAdmin(request, response)) {
    		return;
    	}
    	
    	HttpSession session = request.getSession(false);
        String accion = request.getParameter("accion");
        String idUsuarioStr = request.getParameter("idUsuario");
        
        try {
            int idUsuario = Integer.parseInt(idUsuarioStr);
            
            // Evitar que el admin se modifique a sí mismo (seguridad básica)
            int idAdminLogueado = (Integer) session.getAttribute("idUsuario");
            if (idUsuario == idAdminLogueado) {
                // Redirigir con error (opcional)
                response.sendRedirect("GestionCuentasServlet?error=self_mod");
                return;
            }

            if ("cambiarRol".equals(accion)) {
                int idNuevoRol = Integer.parseInt(request.getParameter("idNuevoRol"));
                usuarioDAO.actualizarRol(idUsuario, idNuevoRol);
                
            } else if ("cambiarEstado".equals(accion)) {
                boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("nuevoEstado"));
                usuarioDAO.cambiarEstadoCuenta(idUsuario, nuevoEstado);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 3. Redirigir al GET para ver los cambios
        response.sendRedirect("GestionCuentasServlet");
    }
}
