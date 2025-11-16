package dao;

/**
 * AuthException (Excepción de Autenticación).
 * Esta es una "Excepción Comprobada" (Checked Exception) que forzará
 * a nuestros Servlets a manejar los errores de login usando un try-catch.
 */
public class AuthException extends Exception {

    /**
     * ID de serialización estándar para Excepciones.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que nos permite crear una nueva AuthException
     * con un mensaje de error específico.
     * * @param mensaje El mensaje de error (ej. "Contraseña incorrecta")
     */
    public AuthException(String mensaje) {
        // Pasa el mensaje al constructor de la clase padre (Exception)
        // Esto permite que luego lo obtengamos con e.getMessage()
        super(mensaje);
    }
}