package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.FichaDetalleProducto;
//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FichaDetalleProductoRegistration extends DataAccessService<FichaDetalleProducto>{
	public FichaDetalleProductoRegistration(){
		super(FichaDetalleProducto.class);
	}
	
	
}
