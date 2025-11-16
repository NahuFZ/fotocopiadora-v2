package servlets;

//Imports de Servlet y Sesión
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; // <-- Importante para archivos

//Imports de I/O (Input/Output) para guardar el archivo
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//Imports de DAO y Modelo
import dao.TrabajoDAO;
import clases.Trabajo;

//Imports de Utilidades
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Para nombres de archivo únicos

/**
 * Servlet que maneja la subida de un nuevo pedido (trabajo).
 * Usa @MultipartConfig para poder manejar la subida de archivos (enctype="multipart/form-data").
 */
@WebServlet("/NuevoPedidoServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
    maxFileSize = 1024 * 1024 * 10, // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class NuevoPedidoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------------------------------
    // ¡¡¡ ATENCIÓN !!! ¡¡¡ IMPORTANTE !!!
    // TODO: Debes cambiar esta ruta a una carpeta REAL en tu computadora
    // donde SÍ tengas permisos de escritura.
    // Ejemplos: "C:/fotocopiadora_uploads/" (Windows)
    //         "/home/usuario/fotocopiadora_uploads/" (Linux/Mac)
    // ¡LA CARPETA DEBE EXISTIR!
    private static final String UPLOADS_DIR = "C:\\Java\\archivos-fotocopiadora";
    // ---------------------------------------------------------------------------------
    
    private TrabajoDAO trabajoDAO;
    
    public NuevoPedidoServlet() {
        super();
        this.trabajoDAO = new TrabajoDAO();
    }

    /**
     * Maneja peticiones GET. Lo redirigimos a POST, aunque
     * idealmente debería solo mostrar el formulario.
     */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Por simplicidad, si alguien intenta acceder por GET,
        // simplemente lo mandamos al login.
        response.sendRedirect("nuevoPedido.jsp");
	}
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<String> errores = new ArrayList<>();
        HttpSession session = request.getSession(false);

        // --- 1. Verificación de Seguridad (Sesión) ---
        if (session == null || session.getAttribute("idUsuario") == null || !"cliente".equals(session.getAttribute("nombreRol"))) {
            request.setAttribute("error", "Acceso denegado. Debe iniciar sesión como cliente.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Obtenemos el ID del cliente logueado desde la sesión
        int idCliente = (Integer) session.getAttribute("idUsuario");
        
        // --- 2. Procesamiento del Formulario Multipart ---
        try {
            // 2.1. Obtener los campos de texto
            String numCopiasStr = request.getParameter("num_copias");
            String calidad = request.getParameter("calidad");
            String faz = request.getParameter("faz");
            String fechaRetiroStr = request.getParameter("fecha_retiro");

            // 2.2. Obtener el archivo (usamos getPart)
            Part filePart = request.getPart("archivo");
            String nombreArchivoOriginal = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            // --- 3. Validación de Datos (Server-Side) ---
            int numCopiasInt = 0;
            Timestamp fechaRetiroTimestamp = null;

            // Validar archivo
            if (nombreArchivoOriginal == null || nombreArchivoOriginal.isBlank()) {
                errores.add("Debe seleccionar un archivo para subir.");
            }
            // (Aquí podrías añadir más validaciones: tipo de archivo, tamaño, etc.)

            // Validar número de copias
            try {
                numCopiasInt = Integer.parseInt(numCopiasStr);
                if (numCopiasInt < 1 || numCopiasInt > 99) {
                    errores.add("El número de copias debe estar entre 1 y 99.");
                }
            } catch (NumberFormatException e) {
                errores.add("El número de copias no es válido.");
            }

            // Validar fecha de retiro
            try {
                LocalDate fechaRetiroDate = LocalDate.parse(fechaRetiroStr);
                if (fechaRetiroDate.isBefore(LocalDate.now())) {
                    errores.add("La fecha de retiro no puede ser en el pasado.");
                }
                // Convertimos LocalDate a Timestamp (a las 00:00:00)
                fechaRetiroTimestamp = Timestamp.valueOf(fechaRetiroDate.atStartOfDay());
            } catch (Exception e) {
                errores.add("La fecha de retiro no es válida.");
            }

            // --- 4. Si hay errores de validación, reenviar ---
            if (!errores.isEmpty()) {
                enviarErrores(request, response, errores);
                return;
            }

            // --- 5. Lógica de Negocio (Guardar Archivo y BBDD) ---
            
            // 5.1. Guardar el archivo en el servidor
            // Generamos un nombre único para evitar colisiones (ej. si dos suben "trabajo.pdf")
            String nombreUnico = UUID.randomUUID().toString() + "_" + nombreArchivoOriginal;
            File uploadsDir = new File(UPLOADS_DIR);
            
            // Verificamos si la carpeta de subida existe (por si acaso)
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs(); // Si no existe, intenta crearla
            }
            
            File archivoGuardado = new File(uploadsDir, nombreUnico);
            
            // Usamos try-with-resources para copiar el stream del archivo
            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, archivoGuardado.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // 5.2. Crear el objeto Trabajo (JavaBean)
            Trabajo nuevoTrabajo = new Trabajo();
            nuevoTrabajo.setIdCliente(idCliente);
            nuevoTrabajo.setNumCopias(numCopiasInt);
            nuevoTrabajo.setCalidad(calidad);
            nuevoTrabajo.setFaz(faz);
            nuevoTrabajo.setFechaRetiroSolicitada(fechaRetiroTimestamp);
            nuevoTrabajo.setNombreArchivoOriginal(nombreArchivoOriginal);
            
            // Guardamos la RUTA COMPLETA donde se guardó el archivo
            nuevoTrabajo.setRutaArchivo(archivoGuardado.getAbsolutePath());

            // 5.3. Llamar al DAO para insertar en la BBDD
            boolean exito = trabajoDAO.crearTrabajo(nuevoTrabajo);

            if (exito) {
                // ¡ÉXITO TOTAL! Redirigimos a la misma página con mensaje
                response.sendRedirect("nuevoPedido.jsp?exito=true");
            } else {
                // Error de BBDD
                errores.add("Error al guardar el pedido en la base de datos.");
                enviarErrores(request, response, errores);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errores.add("Ocurrió un error inesperado: " + e.getMessage());
            enviarErrores(request, response, errores);
        }
    }

    /**
     * Función de ayuda para reenviar al usuario a la página del formulario
     * con una lista de mensajes de error.
     */
    private void enviarErrores(HttpServletRequest request, HttpServletResponse response, List<String> errores) 
            throws ServletException, IOException {
        
        request.setAttribute("listaErrores", errores);
        request.getRequestDispatcher("nuevoPedido.jsp").forward(request, response);
    }
}