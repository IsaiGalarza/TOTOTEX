package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.DetalleOrdenProduccion;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Stateless
public class DetalleOrdenProduccionRegistration extends DataAccessService<DetalleOrdenProduccion>{
	
	public DetalleOrdenProduccionRegistration(){
		super(DetalleOrdenProduccion.class);
	}
	
}
