package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.Atributo;
/**
 * @author Arturo.Herrera
 *
 */
@Stateless
public class AtributoRegistration extends DataAccessService<Atributo>{
	
	public AtributoRegistration(){
		super(Atributo.class);
	}
}
