package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.OrdenProduccion;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Stateless
public class OrdenProduccionRegistration extends DataAccessService<OrdenProduccion>{
	
	public OrdenProduccionRegistration(){
		super(OrdenProduccion.class);
	}
	
}
