package bo.buffalo.structure;

import java.io.Serializable;
import java.util.Date;

import bo.buffalo.model.ProductoProveedor;

public class StructureCardexProducto  implements Serializable{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7808976171143356414L;
	private Integer idProducto;
	private String nombreProducto;
	private double ingreso;
	private double egreso;
	private double stock;
	private Date fecha;
	private String transaccion;
	private String usuario;
	
	
	
	
	public StructureCardexProducto(Integer idProducto, String nombreProducto,
			double ingreso, double egreso, double stock, Date fecha,
			String transaccion, String usuario) {
		super();
		this.idProducto = idProducto;
		this.nombreProducto = nombreProducto;
		this.ingreso = ingreso;
		this.egreso = egreso;
		this.stock = stock;
		this.fecha = fecha;
		this.transaccion = transaccion;
		this.usuario = usuario;
	}
	public Integer getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}
	public double getIngreso() {
		return ingreso;
	}
	public void setIngreso(double ingreso) {
		this.ingreso = ingreso;
	}
	public double getEgreso() {
		return egreso;
	}
	public void setEgreso(double egreso) {
		this.egreso = egreso;
	}
	public double getStock() {
		return stock;
	}
	public void setStock(double stock) {
		this.stock = stock;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	
	
	
}
