package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.ReciboLavanderia;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Stateless
public class ReciboLavanderiaRegistration extends DataAccessService<ReciboLavanderia>{
	
	public ReciboLavanderiaRegistration(){
		super(ReciboLavanderia.class);
	}
	
}
