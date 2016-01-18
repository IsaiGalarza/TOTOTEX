package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.ProcesoLavanderia;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Stateless
public class ProcesoLavanderiaRegistration extends DataAccessService<ProcesoLavanderia>{
	
	public ProcesoLavanderiaRegistration(){
		super(ProcesoLavanderia.class);
	}
	
}
