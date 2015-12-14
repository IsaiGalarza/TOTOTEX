/*
* UserRoleRegistration.java	1.0 2014/09/19
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
package bo.buffalo.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.UsuarioRol;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class UsuarioRolRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<UsuarioRol> userRoleEventSrc;

    public void register(UsuarioRol usuarioRol) throws Exception {
        log.info("Registering usuarioRol: " );
        em.persist(usuarioRol);
        userRoleEventSrc.fire(usuarioRol);
    }
    
    public void updated(UsuarioRol usuarioRol) throws Exception {
    	log.info("Updated usuarioRol ");
        em.merge(usuarioRol);
        userRoleEventSrc.fire(usuarioRol);
    }
    
    public void remover(UsuarioRol usuarioRol){
    	log.info("Remover usuarioRol: " );
        em.merge(usuarioRol);
        userRoleEventSrc.fire(usuarioRol);
    }
}
