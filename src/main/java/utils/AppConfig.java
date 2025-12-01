package utils;

import java.io.File;

public class AppConfig {
    
    // Usamos System.getProperty("user.home") para obtener la carpeta del usuario actual.
    // Esto funciona en Windows, Linux y Mac.
    private static final String USER_HOME = System.getProperty("user.home");
    
    // Definimos el nombre de nuestra carpeta
    private static final String APP_FOLDER_NAME = "archivos-fotocopiadora";
    
    // Construimos la ruta completa de forma dinámica.
    // File.separator pone "\" en Windows y "/" en Linux o Mac automáticamente.
    public static final String DIRECTORIO_ARCHIVOS = USER_HOME + File.separator + APP_FOLDER_NAME;
    
    // Bloque estático para asegurar que la carpeta exista al arrancar la app
    static {
        File dir = new File(DIRECTORIO_ARCHIVOS);
        if (!dir.exists()) {
            boolean creado = dir.mkdirs();
            if (creado) {
                System.out.println("Carpeta de subidas creada en: " + DIRECTORIO_ARCHIVOS);
            } else {
                System.err.println("ADVERTENCIA: No se pudo crear la carpeta de subidas en: " + DIRECTORIO_ARCHIVOS);
            }
        } else {
            System.out.println("Usando carpeta de subidas existente: " + DIRECTORIO_ARCHIVOS);
        }
    }
}
