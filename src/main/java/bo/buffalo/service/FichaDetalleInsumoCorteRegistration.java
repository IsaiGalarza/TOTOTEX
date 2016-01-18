package bo.buffalo.service;

import javax.ejb.Stateless;

import bo.buffalo.model.FichaInsumoCorte;
//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FichaDetalleInsumoCorteRegistration extends DataAccessService<FichaInsumoCorte>{
	public FichaDetalleInsumoCorteRegistration(){
		super(FichaInsumoCorte.class);
	}
	
	
}
