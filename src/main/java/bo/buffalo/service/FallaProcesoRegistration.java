package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.FallaProceso;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FallaProcesoRegistration extends DataAccessService<FallaProceso>{
	public FallaProcesoRegistration(){
		super(FallaProceso.class);
	}
	
	
}
