/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bo.buffalo.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.management.relation.Role;
import javax.persistence.EntityManager;

import bo.buffalo.model.Roles;
import bo.buffalo.model.Usuario;
import bo.buffalo.model.UsuarioRol;
import bo.buffalo.structure.EstructuraRoles;

import java.util.List;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class UsuarioRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Usuario> userEventSrc;
    
    @Inject
    private Event<UsuarioRol> userRoleEventSrc;
    
    public void register(Usuario user) throws Exception {
        log.info("Usuario Registrado: " + user.getName());
        em.persist(user);
        userEventSrc.fire(user);
    }
    
    public void register(Usuario user, List<Roles> listRole) throws Exception {
        try {
        	log.info("Usuario Registrado: " + user.getName());
            em.persist(user);
            userEventSrc.fire(user);
            
            //register role for User
            for(Roles roles: listRole){
            	registerUserRole(user, roles);
            }
		} catch (Exception e) {
			// TODO: handle exception
			log.severe("Error en register: " + e.getMessage());
		}
    }
    
    public void registerUserAndRoles(Usuario user, List<EstructuraRoles> estructuraRoles) throws Exception {
        try {
        	log.info("Usuario Registrado: " + user.getName());
            em.persist(user);
            userEventSrc.fire(user);
            
            //register role for User
            for(EstructuraRoles estructura: estructuraRoles){
            	if(estructura.isCheck()){
            		registerUserRole(user, estructura.getRol());
            	}
            }
		} catch (Exception e) {
			// TODO: handle exception
			log.severe("Error en register: " + e.getMessage());
		}
    }
    
    public void removerUsuarioRol(UsuarioRol userRol) throws Exception {
        log.info("Usuario Registrado: " + userRol.getUsuario().getName()+"- Rol: "+userRol.getRoles().getName());
        em.remove(userRol);
        userRoleEventSrc.fire(userRol);
    }
    
    public void remover(Usuario user) throws Exception {
        	log.info("Usuario Remover: " + user.getName());
        	user.setEstado("RM");
            em.merge(user);
            userEventSrc.fire(user);
    }
            
    public void updateUsuario(Usuario user){
    	log.info("Usuario Updated: " + user.getName());
        em.merge(user);
        userEventSrc.fire(user);
    }
    
    public void update(Usuario user, List<Roles> listRole) throws Exception {
        try {
        	log.info("Usuario Updated: " + user.getName());
            em.merge(user);
            userEventSrc.fire(user);
            
            List<UsuarioRol> listaUsuarioRol = buscarUsuarioRol(user);
            //remover roles actuales
            for(UsuarioRol userRol : listaUsuarioRol){
            	removerUsuarioRol(userRol);
            }
            
            //registrar roles al usuario
            for(Roles roles: listRole){
            	registerUserRole(user, roles);
            }

		} catch (Exception e) {
			// TODO: handle exception
			log.severe("Error en update: " + e.getMessage());
		}
    }
    
    public void updateUserAndRoles(Usuario user, List<EstructuraRoles> listaEstructuraRoles) throws Exception {
        try {
        	log.info("Usuario Updated: " + user.getName());
            em.merge(user);
            userEventSrc.fire(user);
            
            List<UsuarioRol> listaUsuarioRol = buscarUsuarioRol(user);
            //remover roles actuales
            for(UsuarioRol userRol : listaUsuarioRol){
            	removerUsuarioRol(userRol);
            }
            
            //registrar roles al usuario
            for(EstructuraRoles estructura: listaEstructuraRoles){
            	if(estructura.isCheck()){
            		registerUserRole(user, estructura.getRol());
            	}
            }

		} catch (Exception e) {
			// TODO: handle exception
			log.severe("Error en update: " + e.getMessage());
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
    
    public void registerUserRole(Usuario user, Roles roles) throws Exception {
        log.info("Rol Registrado: " + user.getName() + ", Role: "+roles.getName());
        UsuarioRol userRole = new UsuarioRol();
        userRole.setUsuario(user);
        userRole.setRoles(roles);
        em.persist(userRole);
        userRoleEventSrc.fire(userRole);
    }
    
    public void updateUserRole(UsuarioRol userRol) throws Exception {
        log.info("RolUser Update: " + userRol.getUsuario().getName()+ ", Role: "+userRol.getRoles().getName());
        em.merge(userRol);
        userRoleEventSrc.fire(userRol);
    }
}
