package bo.buffalo.structure;

import java.io.Serializable;

import bo.buffalo.model.DetallePedidoMov;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Proveedor;

public class StructuraEntregaTraspaso implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8767635550066929243L;
	private int id;
	private DetallePedidoMov detalle;
	
	private boolean active;
	
	
	
	public StructuraEntregaTraspaso(int id, DetallePedidoMov detalle, boolean active) {
		super();
		this.id = id;
		this.detalle= detalle;
		this.active = active;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public DetallePedidoMov getDetalle() {
		return detalle;
	}
	public void setDetalle(DetallePedidoMov detalle) {
		this.detalle = detalle;
	}
}


