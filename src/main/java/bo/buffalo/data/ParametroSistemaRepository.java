package bo.buffalo.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.ParametroSistema;

@ApplicationScoped
public class ParametroSistemaRepository {
	
    private @Inject EntityManager em;
    
    public ParametroSistema findById(int id) {
        return em.find(ParametroSistema.class, id);
    }
    
    public List<ParametroSistema> findAllOrderedById() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ParametroSistema> criteria = cb.createQuery(ParametroSistema.class);
        Root<ParametroSistema> parametroSistema = criteria.from(ParametroSistema.class);
        criteria.select(parametroSistema).orderBy(cb.desc(parametroSistema.get("id")));
        return em.createQuery(criteria).getResultList();
    }
    
    public ParametroSistema findByKey(String key){
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ParametroSistema> criteria = cb.createQuery(ParametroSistema.class);
        Root<ParametroSistema> parametroSistema = criteria.from(ParametroSistema.class);
        criteria.select(parametroSistema).where(cb.equal(parametroSistema.get("key"), key));
        return em.createQuery(criteria).getSingleResult();
    }
}

