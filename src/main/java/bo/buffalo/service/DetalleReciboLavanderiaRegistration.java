package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.DetalleReciboLavanderia;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class DetalleReciboLavanderiaRegistration extends DataAccessService<DetalleReciboLavanderia>{
	public DetalleReciboLavanderiaRegistration(){
		super(DetalleReciboLavanderia.class);
	}
	
	
}
