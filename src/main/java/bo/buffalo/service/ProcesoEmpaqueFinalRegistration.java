package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.ProcesoEmpaqueFinal;
/**
 * 
 * @author mauriciobejaranorivera
 *
 */
@Stateless
public class ProcesoEmpaqueFinalRegistration extends DataAccessService<ProcesoEmpaqueFinal>{
	
	public ProcesoEmpaqueFinalRegistration(){
		super(ProcesoEmpaqueFinal.class);
	}
	
}
