package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.Lavanderia;


//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class LavanderiaRegistration extends DataAccessService<Lavanderia>{
	public LavanderiaRegistration(){
		super(Lavanderia.class);
	}
	
	
}
