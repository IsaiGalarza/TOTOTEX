/*
* ServicioRepository.java	1.0 2014/09/19
*
* Copyright (c) 2014 by QBIT SRL, Inc. All Rights Reserved.
* 
* QBIT SRL grants you ("Licensee") a non-exclusive, royalty free, license to use,
* modify and redistribute this software in source and binary code form,
* provided that i) this copyright notice and license appear on all copies of
* the software; and ii) Licensee does not utilize the software in a manner
* which is disparaging to QBIT SRL.
* 
* Autor: Isai Galarza
* 
*/
package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.Roles;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;
import bo.buffalo.model.UsuarioRol;

import java.util.List;

@ApplicationScoped
public class UsuarioRepository {

    @Inject
    private EntityManager em;

    public Usuario findById(int id) {
        return em.find(Usuario.class, id);
    }

    public Usuario findByID(int usuarioId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> user = criteria.from(Usuario.class);
        criteria.select(user).where(cb.equal(user.get("id"), usuarioId));
        return em.createQuery(criteria).getSingleResult();
    }
    
    public Usuario findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> user = criteria.from(Usuario.class);
        criteria.select(user).where(cb.equal(user.get("email"), email));
        return em.createQuery(criteria).getSingleResult();
    }
    
    public Usuario findByLogin(String login) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> user = criteria.from(Usuario.class);
        criteria.select(user).where(cb.equal(user.get("login"), login));
        return em.createQuery(criteria).getSingleResult();
    }
    
    public Usuario findByLogin(String login, String password) {
    	try {
    		CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
            Root<Usuario> user = criteria.from(Usuario.class);
            criteria.select(user).where(cb.equal(user.get("login"), login),cb.equal(user.get("password"), password));
            return em.createQuery(criteria).getSingleResult();
		} catch (Exception e) {
			return null;
		}
        
    }

    public List<Usuario> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> user = criteria.from(Usuario.class);
        criteria.select(user).orderBy(cb.asc(user.get("name")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Usuario> traerUsuariosVisitadores() {
        String query = "select us from Usuario us where us.estado='AC' and upper(us.cargo.nombre)  like '%VISITADOR MED%'  order by us.name asc";
        System.out.println("Query traerUsuariosVisitadores: "+query);
        return em.createQuery(query).getResultList();
    }
    
    public List<Usuario> traerPersonalCargoVisitadorMedico() {
        String query = "select usr.usuario from UsuarioRol usr where usr.usuario.estado='AC' and upper(usr.roles.name)='VISITADOR' order by usr.id desc";
        System.out.println("Query Traer Usuarios  de lavoratorio traerUsuariosDeLaboratorio: "+query);
        return em.createQuery(query).getResultList();
    }
    
    public List<Usuario> findAllOrderedByID() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> user = criteria.from(Usuario.class);
        criteria.select(user).orderBy(cb.desc(user.get("id")));
        return em.createQuery(criteria).getResultList();
        
        
    }
    
    public List<Usuario> traerUsuariosActivosInactivos() {
        String query = "select usr from Usuario usr where usr.estado='AC' or usr.estado='IN' order by usr.fechaRegistro desc";
        System.out.println("Query Traer Usuarios Activos/Inactivos: "+query);
        return em.createQuery(query).getResultList();
    }
    
    public List<Usuario> traerUsuariosDeLaboratorio() {
        String query = "select usr.usuario from UsuarioRol usr where usr.usuario.estado='AC' and upper(usr.roles.name)='LABORATORIO' order by usr.id desc";
        System.out.println("Query Traer Usuarios  de lavoratorio traerUsuariosDeLaboratorio: "+query);
        return em.createQuery(query).getResultList();
    }
    
    public List<Usuario> traerUsuariosPorSucursal(Sucursal sucursal) {
    	String query = "select usr from Usuario usr where usr.estado='AC' or usr.estado='IN' and usr.sucursal.id="+sucursal.getId()+" order by usr.id desc";
    	System.out.println("Query Traer Usuarios Activos/Inactivos: "+query);
    	return em.createQuery(query).getResultList();
    	}
    
    public boolean buscarUsuarioRol(Usuario usuario, Roles rol){
    	try {
			String query = "select count(ur) from UsuarioRol ur where ur.usuario.id="+usuario.getId()+ " and ur.roles.id="+rol.getId();
			System.out.println("Query buscarUsuarioRol: "+query);
			Long resultado = (Long) em.createQuery(query).getSingleResult();
			return resultado>0;
			
		} catch (Exception e) {
			System.out.println("Error en buscarUsuarioRol: "+e.getMessage());
			return false;
		}
    }

	public List<UsuarioRol> buscarUsuarioRol(Usuario usuario){
    	try {
			String query = "select ur from UsuarioRol ur where ur.usuario.id="+usuario.getId();
			System.out.println("Query UserRol: "+query);
			return em.createQuery(query).getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en buscarUsuarioRol: "+e.getMessage());
			return null;
		}
    }
	
	public List<Usuario> buscarUsuarioCAJEROS(){
    	try {
			String query = "select us from Usuario us, Cargo c where  c.id=us.cargo.id  and  upper(us.cargo.nombre)='CAJERO'";
			System.out.println("Query UserRol: "+query);
			return em.createQuery(query).getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en buscarUsuarioRol: "+e.getMessage());
			return null;
		}
    }
}