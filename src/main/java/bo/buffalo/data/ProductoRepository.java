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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


import bo.buffalo.model.Almacen;
import bo.buffalo.model.Producto;

@ApplicationScoped
public class ProductoRepository {
	
	@Inject
    private EntityManager em;

    public Producto findById(int id) {
        return em.find(Producto.class, id);
    }

    public List<Producto> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Producto> criteria = cb.createQuery(Producto.class);
        Root<Producto> producto = criteria.from(Producto.class);
        criteria.select(producto).orderBy(cb.asc(producto.get("nombreProducto")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Producto> findAllProductsTypeMP() {
    	String query = "select pro from Producto pro where (pro.estado='AC' or pro.estado='IN') and pro.tipoProducto.descripcion='MATERIA PRIMA' order by pro.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Producto> traerProductosFarmacos() {
    	try {
    		System.out.println("Ingreso a traerProductosFarmacos...");
    		String query = "select pro from Producto pro where pro.estado='AC' and pro.tipoProducto.descripcion='FARMACO' order by pro.id desc";
        	System.out.println("Query Producto Farmaco: "+query);
        	return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductosFarmacos: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Producto> findAllOrderedByID() {
    	String query = "select pro from Producto pro where pro.estado='AC' or pro.estado='IN' order by pro.id desc";
    	System.out.println("Query Servicios: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    @SuppressWarnings("unchecked")
   	public List<Producto> findAllOrderedByIDMatPrima() {
       	String query = "select pro from Producto pro where (pro.estado='AC' or pro.estado='IN') and pro.tipoProducto.descripcion='MATERIA PRIMA' order by pro.id desc";
       	System.out.println("Query Servicios: "+query);
       	return em.createQuery(query).getResultList();
       }
    
    public List<Producto> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Producto> criteria = cb.createQuery(Producto.class);
        Root<Producto> producto = criteria.from(Producto.class);
        criteria.select(producto).orderBy(cb.desc(producto.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<Producto> findAllProductosForDescription(String criterio) {
        try {
        	String query = "select pro from Producto pro where upper(pro.nombreProducto) like '%"+criterio.toUpperCase()+"%'";
        	System.out.println("Consulta: "+query);
        	List<Producto> listaRepuesto = em.createQuery(query).setMaxResults(10).getResultList();
            System.out.println("Resultado: "+listaRepuesto.size());
            return listaRepuesto;  
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllProductosForDescription: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Producto> buscarProductoNombre(String nombreProducto) {
        try {
        	String query = "select pro from Producto pro where pro.estado = 'AC' and upper(pro.nombreProducto) like '"+nombreProducto.toUpperCase()+"%'";
        	System.out.println("Consulta: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllProductosForDescription: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
 	public List<Producto> buscarProductoPreElaboradoNombre(String nombreProducto) {
         try {
         	String query = "select pro from Producto pro where pro.estado = 'AC' and pro.tipoProducto.descripcion='PRE ELABORADO MEDIPIEL' and upper(pro.nombreProducto) like '"+nombreProducto.toUpperCase()+"%'";
         	System.out.println("Consulta: "+query);
             return em.createQuery(query).getResultList();
 		} catch (Exception e) {
 			// TODO: handle exception
 			System.out.println("Error en findAllProductosForDescription: "+e.getMessage());
 			return null;
 		}
     }
    
    
    @SuppressWarnings("unchecked")
 	public List<Producto> buscarProductoPreElaboradoNombre(String nombreProducto,Almacen almacen) {
         try {
         	String query = "select pro from AlmProducto pro where pro.producto.almacen.id="+almacen.getId()+" and pro.producto.estado = 'AC' and pro.producto.tipoProducto.descripcion='PRE ELABORADO MEDIPIEL' and upper(pro.producto.nombreProducto) like '"+nombreProducto.toUpperCase()+"%'";
         	System.out.println("Consulta: "+query);
             return em.createQuery(query).getResultList();
 		} catch (Exception e) {
 			// TODO: handle exception
 			System.out.println("Error en findAllProductosForDescription: "+e.getMessage());
 			return null;
 		}
     }
     @SuppressWarnings("unchecked")
	public List<Producto> buscarProductoNombreExacto(String nombreProducto) {
        try {
        	String query = "select pro from Producto pro where pro.estado = 'AC' and upper(pro.nombreProducto) = '"+nombreProducto.toUpperCase()+"'";
        	System.out.println("Consulta: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllProductosForDescription: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
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
	public List<Producto> traerMateriaPrima() {
		try {
			String query = "select pro from Producto pro where pro.estado='AC' and (pro.tipoProducto.descripcion='MATERIA PRIMA' or pro.tipoProducto.descripcion='PRE ELABORADO MEDIPIEL') order by pro.id desc";
			System.out
					.println("[traerMateriaPrima] Consulta traerMateriaPrima: "
							+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductos: " + e.getMessage());
			return null;
		}
	}
	
	//TRAER PRODUCTOS TIPO ENVASE
	@SuppressWarnings("unchecked")
	public List<Producto> traerProductosEnvase() {
		try {
			String query = "select pro from Producto pro where pro.estado='AC' and pro.tipoProducto.descripcion='ENVASE' order by pro.id desc";
			System.out
					.println("[traerMateriaPrima] Consulta traerMateriaPrima: "
							+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductos: " + e.getMessage());
			return null;
		}
	}
	
    @SuppressWarnings("unchecked")
	public List<Producto> findAllMateriaPrimaForDescription(String criterio) {
        try {
        	String query = "select pro from Producto pro where pro.estado='AC' and (pro.tipoProducto.descripcion='MATERIA PRIMA' or pro.tipoProducto.descripcion='PRE ELABORADO MEDIPIEL') AND  upper(pro.nombreProducto) like '%"+criterio+"%'";
        	System.out.println("Consulta: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllProductosForDescription: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<Producto> traerProdExternosInternos() {
		try {
			String query = "select pro from Producto pro where pro.estado='AC' and (pro.tipoProducto.descripcion='PRE ELABORADO MEDIPIEL' or pro.tipoProducto.descripcion='PRE ELABORADO EXTERNO' or pro.tipoProducto.descripcion='MATERIA PRIMA') order by pro.id desc";
			System.out
					.println("[traerProdExternosInternos] Consulta traerMateriaPrima: "
							+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProdExternosInternos: " + e.getMessage());
			return null;
		}
	}    
    
    @SuppressWarnings("unchecked")
	public List<Producto> traerProdExternosInternosExcludeProdId(Integer idProd) {
		try {
			String query = "select pro from Producto pro where pro.estado='AC'and pro.id<>"+idProd+" and  (pro.tipoProducto.descripcion='PRE ELABORADO MEDIPIEL' or pro.tipoProducto.descripcion='PRE ELABORADO EXTERNO') order by pro.id desc";
			System.out
					.println("[traerProdExternosInternos] Consulta traerProdExternosInternosExcludeProdId: "
							+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProdExternosInternosExcludeProdId: " + e.getMessage());
			return null;
		}
	}
    
    
    @SuppressWarnings("unchecked")
   	public List<Producto> findAllExterInterForDescription(String criterio) {
           try {
           	String query = "select pro from Producto pro where pro.estado='AC' and (pro.tipoProducto.descripcion='PRE ELABORADO EXTERNO' or pro.tipoProducto.descripcion='PRE ELABORADO MEDIPIEL' or pro.tipoProducto.descripcion='MATERIA PRIMA' ) AND  upper(pro.nombreProducto) like '%"+criterio+"%'";
           	System.out.println("Consulta: "+query);
               return em.createQuery(query).getResultList();
   		} catch (Exception e) {
   			// TODO: handle exception
   			System.out.println("Error en findAllProductosForDescription: "+e.getMessage());
   			return null;
   		}
       }
    
	
}
