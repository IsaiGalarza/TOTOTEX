package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.ParametroSistema;

@Stateless
public class ParametroSistemaRegistration extends DataAccessService<ParametroSistema>{
	
	public ParametroSistemaRegistration() {
		super(ParametroSistema.class);
	}
	
}
