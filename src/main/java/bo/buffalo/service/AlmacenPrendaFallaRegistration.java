package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.DetalleAlmacenPrenda;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class AlmacenPrendaFallaRegistration extends DataAccessService<DetalleAlmacenPrenda>{
	public AlmacenPrendaFallaRegistration(){
		super(DetalleAlmacenPrenda.class);
	}
	
	
}
