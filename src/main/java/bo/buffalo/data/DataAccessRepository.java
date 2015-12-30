package bo.buffalo.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


/**
 * @author Arturo.Herrera
 *
 */
public abstract class DataAccessRepository<T> {
	private @Inject EntityManager em;
    private Class<T> type;
    
    public DataAccessRepository() {
    }
    
    public DataAccessRepository(Class<T> type) {
        this.type=type;
    }
	
	public T findById(Object id) {
        return this.em.find(this.type, id);
    }
	
	public List<T> findAllOrderedByID() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList(); 
    }
	
	public List<T> findAllOrderedByID(Integer max) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).setMaxResults(max).getResultList(); 
    }
	
	public List<T> findAllOrderedByParameter(String parameter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).orderBy(cb.asc(object.get(parameter)));
        return em.createQuery(criteria).getResultList();
    }
	
	public List<T> findAllByEstado(boolean estado) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.equal(object.get("estado"), estado)).orderBy(cb.asc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
	
	
	
	public T findByParameter(String parameter,String valor) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<T> criteria = cb.createQuery(this.type);
	        Root<T> object = criteria.from(this.type);
	        criteria.select(object).where(cb.equal(object.get(parameter), valor));
	        return em.createQuery(criteria).getSingleResult();
		} catch (Exception e) {
			System.out.println("Error optener parametro : "+ e.getMessage());
			return null;
		}
        
    }
	
	public T findByParameterObject(String parameter,Object valor) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<T> criteria = cb.createQuery(this.type);
	        Root<T> object = criteria.from(this.type);
	        criteria.select(object).where(cb.equal(object.get(parameter), valor));
	        return em.createQuery(criteria).getSingleResult();
		} catch (Exception e) {
			System.out.println("Error optener parametro : "+ e.getMessage());
			return null;
		} 
    }
	
	public List<T> findAllByParameterIsNull(String parameter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.isNull(object.get(parameter))).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
	
	public List<T> findAllByParameterIsNotNull(String parameter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.isNotNull(object.get(parameter))).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
	
	public List<T> findAllByParameterObject(String parameter,Object valor) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.equal(object.get(parameter), valor)).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
	
	public List<T> findAllByParameterObjectOrder(String parameter,Object valor,String order) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.equal(object.get(parameter), valor)).orderBy(cb.asc(object.get(order)));
        return em.createQuery(criteria).getResultList();
    }
	
	public List<T> findAllByParameterObjectTwo(String parameter,String parameterTwo,Object valor,Object valorTwo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.equal(object.get(parameter), valor),cb.equal(object.get(parameterTwo), valorTwo)).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
	
	public List<T> findAllByParameterObjectMax(String parameter,Object valor,Integer max) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.equal(object.get(parameter), valor)).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).setMaxResults(max).getResultList();
    }
	
	public List<T> findAllByParameterDate(String parameter,Date valor) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(this.type);
        Root<T> object = criteria.from(this.type);
        criteria.select(object).where(cb.equal(object.get(parameter), valor)).orderBy(cb.desc(object.get("id")));
        return em.createQuery(criteria).getResultList();
    }
	
		
	public T findByParameterTwo(String parameterOne,String parameterTwo,Object valorOne,Object valorTwo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<T> criteria = cb.createQuery(this.type);
	        Root<T> object = criteria.from(this.type);
	        criteria.select(object).where(cb.equal(object.get(parameterOne), valorOne),cb.equal(object.get(parameterTwo), valorTwo));
	        return em.createQuery(criteria).getSingleResult();
		} catch (Exception e) {
			return null;
		}
    }
	
	public List<T> findByParameterTwoList(String parameterOne,String parameterTwo,String valorOne,String valorTwo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<T> criteria = cb.createQuery(this.type);
	        Root<T> object = criteria.from(this.type);
	        criteria.select(object).where(cb.equal(object.get(parameterOne), valorOne),cb.equal(object.get(parameterTwo), valorTwo)).orderBy(cb.desc(object.get("id")));
	        return em.createQuery(criteria).getResultList();
		} catch (Exception e) {
			return null;
		}
    }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeList(String parameterDate,Date dateOne,Date dateTwo) {
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+ " order by fac."+parameterDate+" desc")
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
    }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeList(String estado,String parameterDate,Object valueEstado,Date dateOne,Date dateTwo) {
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+estado+"=:stEstado and fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+ " order by fac."+parameterDate+" desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
    }
	
	@SuppressWarnings("unchecked")
	public List<T> findByParameterObjectLevelList(String parameter,Object valor) {
		try {
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+"and fac."+parameter+"=:stObject "+ "order by fac.id desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+"fac."+parameter+"=:stObject"+ " order by fac.id desc")
					.setParameter("stObject", valor)
					.getResultList();
		} catch (Exception e) {
			System.out.println("Error optener parametro : "+ e.getMessage());
			return null;
		} 
    }
	
	@SuppressWarnings("unchecked")
	public List<T> findByParameterObjectLevelList(String parameter,String parameterTwo,Object valor,Object valorTwo) {
		try {
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+" fac."+parameter+"=:stObject "+ "order by fac.id desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+"fac."+parameter+"=:stObject and fac."+parameterTwo+"=:stObjectTwo "+ " order by fac.id desc")
					.setParameter("stObject", valor)
					.setParameter("stObjectTwo", valorTwo)
					.getResultList();
		} catch (Exception e) {
			System.out.println("Error optener parametro : "+ e.getMessage());
			return null;
		} 
    }
	
	@SuppressWarnings("unchecked")
	public List<T> findByParameterObjectLevelList(String estado,String parameter,String parameterTwo,Object valueEstado,Object valor,Object valorTwo) {
		try {
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+" fac."+parameter+"=:stObject "+ "order by fac.id desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+"fac."+estado+"=:stEstado and fac."+parameter+"=:stObject and fac."+parameterTwo+"=:stObjectTwo "+ " order by fac.id desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stObject", valor)
					.setParameter("stObjectTwo", valorTwo)
					.getResultList();
		} catch (Exception e) {
			System.out.println("Error optener parametro : "+ e.getMessage());
			return null;
		} 
    }
	
	@SuppressWarnings("unchecked")
	public List<T> findByParameterObjectLevelList(String estado,String parameter,String parameterTwo,String parameterTree,Object valueEstado,Object valor,Object valorTwo,Object valorTree) {
		try {
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+" fac."+parameter+"=:stObject "+ "order by fac.id desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE "
					+"fac."+estado+"=:stEstado and fac."+parameter+"=:stObject and fac."+
					parameterTwo+"=:stObjectTwo and fac."+parameterTree +"=:stObjectTree order by fac.id desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stObject", valor)
					.setParameter("stObjectTwo", valorTwo)
					.setParameter("stObjectTree", valorTree)
					.getResultList();
		} catch (Exception e) {
			System.out.println("Error optener parametro : "+ e.getMessage());
			return null;
		} 
    }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeObjetcStringList(String estado,String parameterDate,String parameterObjetc,String parameter,Object valueEstado,Date dateOne,Date dateTwo
			,Object valueObject,String valueParameter) {
		List<T> error=new ArrayList<>();
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject and fac."+parameter+"=:stParameter"+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+estado+"=:stEstado and fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject and fac."+parameter+"=:stParameter"+ " order by fac."+parameterDate+" desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.setParameter("stObject", valueObject)
					.setParameter("stParameter", valueParameter)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return error;
		}
    }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeObjetcStringList(String estado,String parameterDate,String parameterObjetc,String parameterObjetcTwo,String parameter,Object valueEstado,Date dateOne,Date dateTwo
			,Object valueObject,Object valueObjectTwo,Object valueParameter) {
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject and fac."+parameterObjetcTwo+"=:stObjectTwo and fac."+parameter+"=:stParameter"+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+estado+"=:stEstado and fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject and fac."+parameterObjetcTwo+"=:stObjectTwo and fac."+parameter+"=:stParameter"+ " order by fac."+parameterDate+" desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.setParameter("stObject", valueObject)
					.setParameter("stObjectTwo", valueObjectTwo)
					.setParameter("stParameter", valueParameter)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
    }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeObjetcList(String parameterDate,String parameterObjetc,Date dateOne,Date dateTwo
			,Object valueObject) {
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject "+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject "+ " order by fac."+parameterDate+" desc")
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.setParameter("stObject", valueObject)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
    }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeObjetcList(String estado,String parameterDate,String parameterObjetc,Object valueEstado,Date dateOne,Date dateTwo
			,Object valueObject) {
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject "+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+estado+"=:stEstado and fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject "+ " order by fac."+parameterDate+" desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.setParameter("stObject", valueObject)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
    }

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeObjetcList(String estado,String parameterDate,String parameterObjetc,String parameterObjetcTwo,Object valueEstado,Date dateOne,Date dateTwo
			,Object valueObject,Object valueObjectTwo) {
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject "+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+estado+"=:stEstado and fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+"and fac."+parameterObjetc+"=:stObject and fac."+parameterObjetcTwo+"=:stObjectTwo " +" order by fac."+parameterDate+" desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.setParameter("stObject", valueObject)
					.setParameter("stObjectTwo", valueObjectTwo)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
    }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> findByParameterDateRangeStringList(String estado,String parameterDate,String parameter,Object valueEstado,Date dateOne,Date dateTwo
			,String valueParameter) {
		try {
			dateTwo.setHours(23); dateTwo.setMinutes(59); dateTwo.setSeconds(59);
			System.out.println("Ingreso a buscar Libro Compras....");
			System.out.println("Consulta: "+"select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+" and fac."+parameter+"=:stParameter"+ " order by fac."+parameterDate+" desc");
			return em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac."+estado+"=:stEstado and fac."+parameterDate+">=:stDate and fac."+parameterDate+"<=:edDate "
					+" and fac."+parameter+"=:stParameter"+ " order by fac."+parameterDate+" desc")
					.setParameter("stEstado", valueEstado)
					.setParameter("stDate", dateOne)
					.setParameter("edDate", dateTwo)
					.setParameter("stParameter", valueParameter)
					.getResultList();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
    }
	
	public List<T> findByParameterTreeBooleanList(String parameterOne,String parameterTwo,String paremeterBoolean,String valorOne,String valorTwo, boolean valorBoolean) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<T> criteria = cb.createQuery(this.type);
	        Root<T> object = criteria.from(this.type);
	        criteria.select(object).where(cb.equal(object.get(parameterOne), valorOne),cb.equal(object.get(parameterTwo), valorTwo)
	        		,cb.equal(object.get(paremeterBoolean), valorBoolean)).orderBy(cb.desc(object.get("id")));
	        return em.createQuery(criteria).getResultList();
		} catch (Exception e) {
			return null;
		}
    }
	
	public T findByParameterObjectTwo(String parameterOne,String parameterTwo,Object valorOne,Object valorTwo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<T> criteria = cb.createQuery(this.type);
	        Root<T> object = criteria.from(this.type);
	        criteria.select(object).where(cb.equal(object.get(parameterOne), valorOne),cb.equal(object.get(parameterTwo), valorTwo));
	        return em.createQuery(criteria).getSingleResult();
		} catch (Exception e) {
			System.out.println("Error optener parametro : "+ e.getMessage());
			return null;
		}
        
    }
	
	//method mauricio
	@SuppressWarnings("unchecked")
	public Integer findCorrelativo() {
		try {
			System.out.println("findCorrelativo() ....");
			 List<T> listT = em.createQuery("select fac FROM "+this.type.getSimpleName()+ " fac WHERE fac.estado='AC' ").getResultList();
			return listT.size()+1;
		} catch (Exception e) {
			System.out.println(e.toString());
			return 0;
		}
    }
}
