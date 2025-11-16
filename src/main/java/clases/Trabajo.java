package clases;

import java.io.Serializable;
import java.sql.Timestamp;

public class Trabajo implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- Atributos de la BBDD ---
    private int idTrabajo;
    private int idCliente; // La clave foránea
    private String rutaArchivo;
    private String nombreArchivoOriginal;
    private int numCopias;
    private String calidad; // 'blanco_y_negro' o 'color'
    private String faz;     // 'simple' o 'doble'
    private String estado;  // 'pendiente', 'terminado' o 'retirado'
    
    // Para las fechas, java.sql.Timestamp es el tipo más directo
    // que devuelve JDBC (rs.getTimestamp())
    private Timestamp fechaSolicitud;
    private Timestamp fechaRetiroSolicitada;
    private Timestamp fechaImpresion;
    private Timestamp fechaEntrega;
    
    // --- Atributos Extra (para JOINs) ---
    // Son muy útiles para mostrar en las vistas (JSP)
    // sin tener que pasar múltiples objetos.
    private String nombreCliente;
	private String emailCliente;
	
    /**
     * Constructor vacío (requerido por JavaBean).
     */
    public Trabajo() {
    }

    // --- Getters y Setters ---
    
    public int getIdTrabajo() {
		return idTrabajo;
	}
	public void setIdTrabajo(int idTrabajo) {
		this.idTrabajo = idTrabajo;
	}
	public int getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	public String getRutaArchivo() {
		return rutaArchivo;
	}
	public void setRutaArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}
	public String getNombreArchivoOriginal() {
		return nombreArchivoOriginal;
	}
	public void setNombreArchivoOriginal(String nombreArchivoOriginal) {
		this.nombreArchivoOriginal = nombreArchivoOriginal;
	}
	public int getNumCopias() {
		return numCopias;
	}
	public void setNumCopias(int numCopias) {
		this.numCopias = numCopias;
	}
	public String getCalidad() {
		return calidad;
	}
	public void setCalidad(String calidad) {
		this.calidad = calidad;
	}
	public String getFaz() {
		return faz;
	}
	public void setFaz(String faz) {
		this.faz = faz;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Timestamp getFechaSolicitud() {
		return fechaSolicitud;
	}
	public void setFechaSolicitud(Timestamp fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}
	public Timestamp getFechaRetiroSolicitada() {
		return fechaRetiroSolicitada;
	}
	public void setFechaRetiroSolicitada(Timestamp fechaRetiroSolicitada) {
		this.fechaRetiroSolicitada = fechaRetiroSolicitada;
	}
	public Timestamp getFechaImpresion() {
		return fechaImpresion;
	}
	public void setFechaImpresion(Timestamp fechaImpresion) {
		this.fechaImpresion = fechaImpresion;
	}
	public Timestamp getFechaEntrega() {
		return fechaEntrega;
	}
	public void setFechaEntrega(Timestamp fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	public String getNombreCliente() {
		return nombreCliente;
	}
	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	public String getEmailCliente() {
		return emailCliente;
	}
	public void setEmailCliente(String emailCliente) {
		this.emailCliente = emailCliente;
	}

	@Override
    public String toString() {
        return "Trabajo [idTrabajo=" + idTrabajo + 
               ", idCliente=" + idCliente + 
               ", nombreArchivoOriginal=" + nombreArchivoOriginal + 
               ", estado=" + estado + 
               ", numCopias=" + numCopias + "]";
    }
}
