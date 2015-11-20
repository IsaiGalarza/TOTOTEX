package bo.buffalo.structure;

import java.io.Serializable;

import bo.buffalo.model.ProductoProveedor;

public class StructureCatalogo  implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5998050639646501024L;
	private ProductoProveedor productoProveedor;

	public ProductoProveedor getProductoProveedor() {
		return productoProveedor;
	}

	public void setProductoProveedor(ProductoProveedor productoProveedor) {
		this.productoProveedor = productoProveedor;
	}
}
