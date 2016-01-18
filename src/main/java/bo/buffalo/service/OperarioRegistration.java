package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.Operario;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class OperarioRegistration extends DataAccessService<Operario>{
	public OperarioRegistration(){
		super(Operario.class);
	}
	
	
}
