package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;

import bo.buffalo.model.Atributo;

/**
 * @author Arturo.Herrera
 *
 */
@ApplicationScoped
public class AtributoRepository extends DataAccessRepository<Atributo>{
	public AtributoRepository(){
		super(Atributo.class);
	}
}
