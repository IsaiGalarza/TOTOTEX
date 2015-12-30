package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.ProcesoCorte;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Stateless
public class ProcesoCorteRegistration extends DataAccessService<ProcesoCorte>{
	
	public ProcesoCorteRegistration(){
		super(ProcesoCorte.class);
	}
	
}
