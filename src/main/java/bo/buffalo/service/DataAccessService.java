package bo.buffalo.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.RollbackException;
import javax.validation.ConstraintViolationException;

import com.ahosoft.utils.FacesUtil;


/**
 * @author Arturo.Herrera
 */

public abstract class DataAccessService<T> {

	@Inject
    private EntityManager em;
    
    @Inject
    private Logger log;
    
    @Inject
    private Event<T> tEventSrc;

    public DataAccessService() {
    }
    
    private Class<T> type;

    /**
     * Default constructor
     * 
     * @param type entity class
     */
    public DataAccessService(Class<T> type) {
        this.type = type;
    }
    
    /**
     * Stores an instance of the entity class in the database
     * @param T Object
     * @return 
     */
    public T register(T t){
    	try {
    		getLog().info("Registering "+ t.toString());
    		this.getEm().persist(t);
            this.getEm().flush();
            this.getEm().refresh(t);
            tEventSrc.fire(t);
            getLog().info("register complet:  "+t);
            return t;
		} catch (Exception e) {
			System.out.println("Error "+e.getCause());
			String cause=e.getMessage();
			if(cause.equals("org.hibernate.exception.ConstraintViolationException: could not execute statement")){
				FacesUtil.errorMessage("Ya existe un registro con el mismo nombre.!");
			}else{
				FacesUtil.errorMessage("Ocurrio un error en el registro.!");
			}
			return null;
		}
    		
    }

    /**
     * Retrieves an entity instance that was previously persisted to the database 
     * @param T Object
     * @param id
     * @return 
     */
    public T find(Object id) throws Exception  {
        return this.getEm().find(this.type, id);
    }

    /**
     * Removes the record that is associated with the entity instance
     * @param type
     * @param id 
     */
    public void remover(Object id)  throws Exception {
        Object ref = this.getEm().getReference(this.type, id);
        this.getEm().remove(ref);
    }


    /**
     * Updates the entity instance
     * @param <T>
     * @param t
     * @return the object that is updated
     */
    public T updated(T item){
    		T t= (T) this.getEm().merge(item);
            getLog().info("update complet:  "+item);
            return t;      
    }

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

}