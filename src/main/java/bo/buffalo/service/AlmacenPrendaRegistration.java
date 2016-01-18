package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.AlmacenPrenda;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class AlmacenPrendaRegistration extends DataAccessService<AlmacenPrenda>{
	public AlmacenPrendaRegistration(){
		super(AlmacenPrenda.class);
	}
	
	
}
