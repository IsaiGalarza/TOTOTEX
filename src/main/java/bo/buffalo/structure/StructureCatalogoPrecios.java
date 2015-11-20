package bo.buffalo.structure;

import java.io.Serializable;

import bo.buffalo.model.ProductoProveedor;

public class StructureCatalogoPrecios  implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3018449110184046895L;
	
	private Integer idProducto;
	private String nombreProducto;
	private double stock;
	
	
	public StructureCatalogoPrecios(Integer idProducto, String nombreProducto,
			double stock) {
		super();
		this.idProducto = idProducto;
		this.nombreProducto = nombreProducto;
		this.stock = stock;
	}
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}
	public Integer getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}
	public double getStock() {
		return stock;
	}
	public void setStock(double stock) {
		this.stock = stock;
	}
}
