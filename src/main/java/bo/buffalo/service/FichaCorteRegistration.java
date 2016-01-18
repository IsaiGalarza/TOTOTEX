package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.FichaCorte;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FichaCorteRegistration extends DataAccessService<FichaCorte>{
	public FichaCorteRegistration(){
		super(FichaCorte.class);
	}
	
	
}
