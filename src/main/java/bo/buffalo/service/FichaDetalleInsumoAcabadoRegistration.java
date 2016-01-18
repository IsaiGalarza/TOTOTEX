package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.FichaDetalleInsumoAcabado;
//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FichaDetalleInsumoAcabadoRegistration extends DataAccessService<FichaDetalleInsumoAcabado>{
	public FichaDetalleInsumoAcabadoRegistration(){
		super(FichaDetalleInsumoAcabado.class);
	}
	
	
}
