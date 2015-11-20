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

import bo.buffalo.model.HistorialCostos;
import bo.buffalo.model.Producto;

import java.util.List;

@ApplicationScoped
public class HistorialCostosRepository {

    @Inject
    private EntityManager em;

    public HistorialCostos findById(int id) {
        return em.find(HistorialCostos.class, id);
    }

    public HistorialCostos findByID(int usuarioId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HistorialCostos> criteria = cb.createQuery(HistorialCostos.class);
        Root<HistorialCostos> user = criteria.from(HistorialCostos.class);
        criteria.select(user).where(cb.equal(user.get("id"), usuarioId));
        return em.createQuery(criteria).getSingleResult();
    }
    

    
    public List<HistorialCostos> traerHistorialCostossActivosInactivos(Producto selectedProducto) {
        String query = "select usr from HistorialCostos usr where (usr.estado='AC' or usr.estado='IN') and usr.producto.id="+selectedProducto.getId()+"  order by usr.fechaRegistro desc";
        System.out.println("Query Traer HistorialCostoss Activos/Inactivos: "+query);
        return em.createQuery(query).getResultList();
    }
    
    public List<HistorialCostos> traerHistorialCostossActivos(Producto selectedProducto) {
        String query = "select usr from HistorialCostos usr where usr.estado='AC' and usr.producto.id="+selectedProducto.getId()+"  order by usr.fechaRegistro desc";
        System.out.println("Query Traer HistorialCostoss Activos/Inactivos: "+query);
        return em.createQuery(query).getResultList();
    }
    
}
