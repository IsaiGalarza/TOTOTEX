package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.FichaInsumoCorte;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FichaInsumoCorteRegistration extends DataAccessService<FichaInsumoCorte>{
	public FichaInsumoCorteRegistration(){
		super(FichaInsumoCorte.class);
	}
	
	
}
