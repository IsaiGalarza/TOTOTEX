package bo.buffalo.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.Dosificacion;
import bo.buffalo.model.Sucursal;

@ApplicationScoped
public class SucursalRepository {
	
	@Inject
    private EntityManager em;

    public Sucursal findById(int id) {
        return em.find(Sucursal.class, id);
    }

    public List<Sucursal> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sucursal> criteria = cb.createQuery(Sucursal.class);
        Root<Sucursal> sucursal = criteria.from(Sucursal.class);
        criteria.select(sucursal).orderBy(cb.desc(sucursal.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Sucursal> findAllOrderedByID() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sucursal> criteria = cb.createQuery(Sucursal.class);
        Root<Sucursal> sucursal = criteria.from(Sucursal.class);
        criteria.select(sucursal).orderBy(cb.desc(sucursal.get("id")));
        return em.createQuery(criteria).getResultList();
    }
	
    @SuppressWarnings("unchecked")
	public List<Sucursal> traerSucursalesActivas() {
        try {
        	String query = "select suc from Sucursal suc where suc.estado='AC' order by suc.id desc";
        	System.out.println("Consulta traerSucursales: "+query);
            return  em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerSucursales: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Sucursal> traerSucursales() {
        try {
        	String query = "select suc from Sucursal suc where suc.estado='AC' or suc.estado='IN' order by suc.id desc";
        	System.out.println("Consulta traerSucursales: "+query);
            return  em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerSucursales: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Sucursal> traerSucursalesFacturas() {
        try {
        	String query = "select suc from Sucursal suc where suc.id in (select distinct fac.idSucursal from Factura fac)";
        	System.out.println("Consulta traerSucursalesFacturas: "+query);
            return  em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerSucursalesFacturas: "+e.getMessage());
			return null;
		}
    }
    
    
    public Dosificacion dosificacionActiva(Sucursal sucursal){
    	try {
    		CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Dosificacion> criteria = cb.createQuery(Dosificacion.class);
            Root<Dosificacion> dosificacion = criteria.from(Dosificacion.class);
            criteria.select(dosificacion).where(cb.equal(dosificacion.get("sucursal"), sucursal)
            		,cb.equal(dosificacion.get("activo"), true)).orderBy(cb.desc(dosificacion.get("id")));
            return em.createQuery(criteria).getSingleResult();
		} catch (Exception e) {
			System.out.println("Error en dosificacionActiva: "+e.getMessage());
			return null;
		}
    }
    
    public List<Dosificacion> dosificacionesActivas(Sucursal sucursal){
    	try {
    		CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Dosificacion> criteria = cb.createQuery(Dosificacion.class);
            Root<Dosificacion> dosificacion = criteria.from(Dosificacion.class);
            criteria.select(dosificacion).where(cb.equal(dosificacion.get("sucursal"), sucursal)
            		,cb.equal(dosificacion.get("baja"), false)).orderBy(cb.desc(dosificacion.get("id")));
            return em.createQuery(criteria).getResultList();
		} catch (Exception e) {
			System.out.println("Error en dosificacionesActivas: "+e.getMessage());
			return null;
		}
    }
}
