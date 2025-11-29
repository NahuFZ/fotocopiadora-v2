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
import utils.AppConfig;
import utils.Utils;

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

import java.sql.SQLException;
//Imports de Utilidades
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        
    	// --- 1. Comprueba sesión abierta de cliente ---
    	if (!Utils.esCliente(request, response)) {
    		return;
    	}
    	
        List<String> errores = new ArrayList<>();
        HttpSession session = request.getSession(false);
        
        // Obtenemos el ID del cliente logueado desde la sesión
        int idCliente = (Integer) session.getAttribute("idUsuario");
        
        // --- Declaramos nuestras variables fuera del try ---
        // para que el bloque catch pueda "verlas" y hacer el rollback.
        File archivoGuardado = null;
        String nombreUnico = null;
        
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
            String mimeType = filePart.getContentType(); // <-- Obtenemos el tipo de archivo real

            // --- 3. Validación de Datos (Server-Side) ---
            int numCopiasInt = 0;
            Timestamp fechaRetiroTimestamp = null;

            // Validar archivo (Existencia)
            if (nombreArchivoOriginal == null || nombreArchivoOriginal.isBlank()) {
                errores.add("Debe seleccionar un archivo para subir.");
            } else {
                // *** ¡NUEVA VALIDACIÓN DE SEGURIDAD! ***
                // Validar el TIPO de archivo (MIME Type)
                if (!"application/pdf".equals(mimeType) && 
                    !"image/jpeg".equals(mimeType) && // Incluye JPG y JPEG.
                    !"image/png".equals(mimeType)) {
                    
                    errores.add("Tipo de archivo no permitido. Solo se aceptan PDF, JPG, JPEG y PNG.");
                    // (Logueamos el tipo de archivo que intentaron subir por seguridad)
                    System.out.println("Intento de subida de archivo no permitido: " + mimeType);
                }
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
            } catch (DateTimeParseException e) {
                errores.add("El formato de fecha no es válido.");
            } catch (NullPointerException e) {
            	errores.add("Ingrese una fecha.");
            }

            // --- 4. Si hay errores de validación, reenviar ---
            if (!errores.isEmpty()) {
                enviarErrores(request, response, errores);
                return;
            }

            // --- 5. Lógica de Negocio (Guardar Archivo y BBDD) ---
            
            // 5.1. Guardar el archivo en el servidor
            // Generamos un nombre único para evitar colisiones (ej. si dos suben "trabajo.pdf")
            // agrupamos un código único pseudoaleatorio junto al nombre del archivo
            nombreUnico = UUID.randomUUID().toString() + "_" + nombreArchivoOriginal;
            if (nombreUnico.length() > 260) {
            	nombreUnico = UUID.randomUUID().toString();
            }
            // USAMOS LA RUTA DE LA CLASE DE CONFIGURACIÓN
            File uploadsDir = new File(AppConfig.DIRECTORIO_ARCHIVOS);
            archivoGuardado = new File(uploadsDir, nombreUnico); // Combinamos Ruta + Nombre
            
            // Usamos try-with-resources para copiar el stream del archivo (es decir, sus datos binarios)
            // Luego los copia en la ubicación designada.
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
            
            // Guardamos el nombre con el que se guardó el archivo
            nuevoTrabajo.setNombreArchivo(nombreUnico);

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

        } catch (IllegalStateException e) {
            // Esta excepción es lanzada por Tomcat si el archivo excede el
            // tamaño definido en @MultipartConfig (ej. maxFileSize)
            e.printStackTrace();
            errores.add("El archivo es demasiado grande. El límite es de 10 MB.");
            enviarErrores(request, response, errores);
            
        } catch (IOException e) {
            // Este error lo lanza Files.copy() si falla el guardado en disco
            // (ej. no hay permisos, no hay espacio en disco)
            e.printStackTrace();
            errores.add("Error al guardar el archivo en el servidor. Contacte al administrador.");
            enviarErrores(request, response, errores);

        } catch (SQLException | ClassNotFoundException e) {
            // Este error lo lanza el DAO (paso 5.3) si la BBDD falla.
            e.printStackTrace();
            
            // --- ¡AQUÍ VA EL ROLLBACK MANUAL! ---
            // Si llegamos aquí, es probable que el archivo SÍ se haya guardado (paso 5.1)
            // pero la BBDD falló. Debemos borrar el archivo huérfano.
            if (archivoGuardado != null && archivoGuardado.exists()) {
                try {
                    Files.delete(archivoGuardado.toPath());
                    System.out.println("ROLLBACK MANUAL: Se eliminó el archivo huérfano: " + nombreUnico);
                } catch (IOException ioex) {
                    System.err.println("ERROR CRÍTICO: Falló el rollback. Archivo huérfano NO eliminado: " + nombreUnico);
                    ioex.printStackTrace();
                }
            }
            
            errores.add("Error de conexión con la base de datos. Su pedido no fue procesado.");
            enviarErrores(request, response, errores);
            
        } catch (Exception e) {
        	// Un catch genérico final por si algo más se escapó (ej. NullPointerException)
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