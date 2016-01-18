package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.Confeccionista;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ConfeccionistaRegistration extends DataAccessService<Confeccionista>{
	public ConfeccionistaRegistration(){
		super(Confeccionista.class);
	}
	
	
}
