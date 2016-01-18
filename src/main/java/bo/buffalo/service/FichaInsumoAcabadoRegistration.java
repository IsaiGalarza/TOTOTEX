package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.FichaInsumoAcabado;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FichaInsumoAcabadoRegistration extends DataAccessService<FichaInsumoAcabado>{
	public FichaInsumoAcabadoRegistration(){
		super(FichaInsumoAcabado.class);
	}
	
	
}
