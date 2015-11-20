package bo.buffalo.structure;

import java.io.Serializable;

import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Proveedor;

public class StructuraVerificacion implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8767635550066929243L;
	private int id;
	private Producto producto;
	private LineasProveedor lineasProveedor;
	private Proveedor proveedor;
	private double stockPedido;
	private double stockExistente;
	private boolean active;
	
	
	
	public StructuraVerificacion(int id, Producto producto,
			LineasProveedor lineasProveedor, Proveedor proveedor,
			double stockPedido, double stockExistente, boolean active) {
		super();
		this.id = id;
		this.producto = producto;
		this.lineasProveedor = lineasProveedor;
		this.proveedor = proveedor;
		this.stockPedido = stockPedido;
		this.stockExistente = stockExistente;
		this.active = active;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	public LineasProveedor getLineasProveedor() {
		return lineasProveedor;
	}
	public void setLineasProveedor(LineasProveedor lineasProveedor) {
		this.lineasProveedor = lineasProveedor;
	}
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	public double getStockPedido() {
		return stockPedido;
	}
	public void setStockPedido(double stockPedido) {
		this.stockPedido = stockPedido;
	}
	public double getStockExistente() {
		return stockExistente;
	}
	public void setStockExistente(double stockExistente) {
		this.stockExistente = stockExistente;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}


