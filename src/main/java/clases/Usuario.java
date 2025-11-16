package clases;

import java.io.Serializable;

/**
 * Clase JavaBean (o POJO, o Modelo) que representa la entidad 'Usuarios'.
 * * Actúa como un contenedor de datos para pasar información del usuario
 * de forma limpia entre las capas de la aplicación (DAO -> Servlet -> Sesión).
 * * Implementa Serializable para poder ser guardada de forma segura en HttpSession.
 */

public class Usuario implements Serializable {
    
    // Es una buena práctica para la serialización.
    private static final long serialVersionUID = 1L;

    // --- Atributos ---
    // Son privados y coinciden con las columnas de la BBDD + datos útiles.
    private int idRol;
    private String email;
    private String nombreCompleto;
    private boolean estaActivo;
    
    
 // Atributo extra: Lo rellenaremos con un JOIN en el DAO.
    // Es útil para que el Servlet sepa el nombre del rol sin hacer otra consulta.
    private String nombreRol;
    
    // NOTA: Intencionalmente NO incluimos la contraseña.
    // El objeto de un usuario logueado NUNCA debe contener el password.

    // --- Constructores ---
    
    /*
     * Constructor vacío.
     * Requerido por la especificación de JavaBean.
     */
    public Usuario() {
    }

    // (Opcional) Se puede añadir un constructor con todos los campos
    // si se desea, pero para POJOs puros, getters/setters son suficientes.

    // --- Getters y Setters ---
    // Métodos públicos para acceder y modificar los atributos privados.
    private int idUsuario;
    public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getIdRol() {
		return idRol;
	}
	public void setIdRol(int idRol) {
		this.idRol = idRol;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public boolean isEstaActivo() {
		return estaActivo;
	}
	public void setEstaActivo(boolean estaActivo) {
		this.estaActivo = estaActivo;
	}
	
	public String getNombreRol() {
			return nombreRol;
	}
	public void setNombreRol(String nombreRol) {
		this.nombreRol = nombreRol;
	}
	
    // --- Método toString() ---
    // (Opcional pero MUY recomendado para depuración)
    // Permite imprimir el objeto en consola y ver su contenido.
    
    @Override
    public String toString() {
        return "Usuario [idUsuario=" + idUsuario + 
               ", email=" + email + 
               ", nombreCompleto=" + nombreCompleto + 
               ", idRol=" + idRol + 
               ", nombreRol=" + nombreRol + 
               ", estaActivo=" + estaActivo + "]";
    }
}