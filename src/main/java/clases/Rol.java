package clases;

import java.io.Serializable;

/**
 * Clase JavaBean (o POJO, o Modelo) que representa la entidad 'Roles'.
 * Actúa como un contenedor de datos para cada rol del sistema.
 */
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- Atributos ---
    // Coinciden con las columnas de la tabla 'roles'
    private int idRol;
    private String nombreRol; // Mapea a 'nombre_rol' en la BBDD
    
    // --- Constructores ---
    
    /**
     * Constructor vacío (requerido por JavaBean).
     */
    public Rol() {
    }

    /**
     * Constructor completo (opcional, pero útil).
     */
    public Rol(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }
    
    // --- Getters y Setters ---

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    // --- Método toString() (para depuración) ---
    
    @Override
    public String toString() {
        return "Rol [idRol=" + idRol + ", nombreRol=" + nombreRol + "]";
    }
}