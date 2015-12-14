package bo.buffalo.data;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.Permiso;
import bo.buffalo.model.Privilegio;
import bo.buffalo.model.Roles;

@ApplicationScoped
public class PrivilegioRepository {

	@Inject
	private EntityManager em;


	public Privilegio findById(int id) {
		return em.find(Privilegio.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Privilegio> findAllByRol(Roles roles) {
		String query = "select em from Privilegio em where em.estado='AC' and em.roles.id="+roles.getId();
		System.out.println("Query Privilegio: " + query);
		return em.createQuery(query).getResultList();
	}

	public Privilegio findByRolAndPermiso(Roles roles,Permiso permiso) {
		try{
			String query = "select em from Privilegio em where em.estado='AC' and em.roles.id="+roles.getId()+" and em.permiso.id="+permiso.getId();
			System.out.println("Query Privilegio: " + query);
			return (Privilegio) em.createQuery(query).getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
	
	public List<Privilegio> findAllByRoles(Roles roles){
		try{
			String query = "select em from Privilegio em where em.estado='AC' and em.roles.id="+roles.getId()+" order by em.id asc";
			System.out.println("Query Privilegio: "+query);
			return em.createQuery(query).getResultList();
		}catch(Exception e){
			System.out.println("error: "+e.getMessage());
			return new ArrayList<Privilegio>();

		}
	}
}