package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;

import bo.buffalo.model.FichaTecnica;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class FichaTecnicaRepository extends DataAccessRepository<FichaTecnica>{
	public FichaTecnicaRepository(){
		super(FichaTecnica.class);
	}
}
