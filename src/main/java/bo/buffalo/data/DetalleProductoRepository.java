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

import java.nio.file.Path;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.DetalleProducto;
import bo.buffalo.model.Producto;



@ApplicationScoped
public class DetalleProductoRepository {
	
	@Inject
    private EntityManager em;

    public DetalleProducto findById(int id) {
        return em.find(DetalleProducto.class, id);
    }

    public List<DetalleProducto> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleProducto> criteria = cb.createQuery(DetalleProducto.class);
        Root<DetalleProducto> DetalleProducto = criteria.from(DetalleProducto.class);
        criteria.select(DetalleProducto).orderBy(cb.desc(DetalleProducto.get("nombreDetalleProducto")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<DetalleProducto> findDetalleProducto(Producto Producto) {
        try {
        	String query = "select deta from DetalleProducto deta where deta.Producto.id="+Producto.getId();
        	System.out.println("Consulta DetalleProducto Composicion: "+query);
            List<DetalleProducto> listaDetalleProducto = em.createQuery(query).getResultList();
            return listaDetalleProducto;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findDetalleProducto: "+e.getMessage());
			return null;
		}
    }
    
    
    
    @SuppressWarnings("unchecked")
	public List<DetalleProducto> findAllOrderedByID() {
    	String query = "select pro from DetalleProducto pro where pro.estado='AC' or pro.estado='IN' order by pro.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    @SuppressWarnings("unchecked")
	public List<DetalleProducto> findAllForProductoOrderedByID(int idProducto) {
    	String query = "select rp from DetalleProducto rp where rp.estado='AC' and rp.producto.id="+idProducto+" order by rp.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
  
    
   
    
    @SuppressWarnings("unchecked")
	public List<DetalleProducto> traerDetalleProductos() {
        try {
        	String query = "select pro from DetalleProducto pro where pro.estado='AC' order by pro.id desc";
        	System.out.println("Consulta traerServicios: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerDetalleProductos: "+e.getMessage());
			return null;
		}
    }
    
    
    
	
}
