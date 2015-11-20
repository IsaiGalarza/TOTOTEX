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

import bo.buffalo.model.Producto;
import bo.buffalo.model.TipoProducto;

@ApplicationScoped
public class ProductopRepository {
	
	@Inject
    private EntityManager em;

    public Producto findById(int id) {
        return em.find(Producto.class, id);
    }

    public List<Producto> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Producto> criteria = cb.createQuery(Producto.class);
        Root<Producto> Producto = criteria.from(Producto.class);
        criteria.select(Producto).orderBy(cb.desc(Producto.get("nombreProducto")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Producto> findAllOrderedByID() {
    	String query = "select pro from Producto pro where pro.estado='AC' or pro.estado='IN' order by pro.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    
    public List<Producto> findAllOrderedByDateRegister() {
    	String query = "select pro from Producto pro where pro.estado='AC' or pro.estado='IN' order by pro.fechaRegistro desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
   	public List<Producto> findAllOrderedByDate() {
       	String query = "select pro from Producto pro where pro.estado='AC' order by pro.fechaRegistro desc";
       	System.out.println("Query Servicios: "+query);
       	return em.createQuery(query).getResultList();
       }
    
    
    @SuppressWarnings("unchecked")
   	public List<Producto> findAllFarmacoOrderedByDate() {
       	String query = "select pro from Producto pro where pro.estado='AC' and  pro.tipoProducto.descripcion='FARMACO' order by pro.fechaRegistro desc";
       	System.out.println("Query Servicios: "+query);
       	return em.createQuery(query).getResultList();
       }
  
    @SuppressWarnings("unchecked")
   	public List<Producto> findAllOrderedByDate(TipoProducto tipo) {
       	String query = "select pro from Producto pro where (pro.estado='AC' or pro.estado='IN') and pro.tipoProducto.descripcion='"+tipo.getDescripcion()+"' order by pro.fechaRegistro desc";
       	System.out.println("Query Servicios: "+query);
       	return em.createQuery(query).getResultList();
       }
   
    
    @SuppressWarnings("unchecked")
	public List<Producto> traerProductos() {
        try {
        	String query = "select pro from Producto pro where pro.estado='AC' order by pro.id desc";
        	System.out.println("Consulta traerServicios: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductos: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public Integer obtenerCorrelativo(TipoProducto tp) {
        try {
        	String query = " select max(pro.correlativo) +1 from Producto pro where pro.estado='AC' and pro.tipoProducto.descripcion='"+tp.getDescripcion()+"'";
   //" select con.codigoProducto from Producto con where con.id in( select max(pro.id) from Producto pro where pro.estado='AC' and pro.tipoProducto.descripcion='MATERIA PRIMA')"
        	System.out.println("Consulta obtenerCorrelativo: "+query);
        	Object obj=em.createQuery(query).getSingleResult();
        	if (obj!=null) {
        		return (Integer) obj;
			}else{
				return 1;
			}
        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductos: "+e.getMessage());
			return 1;
		}
    }
    
    
    
	
}
