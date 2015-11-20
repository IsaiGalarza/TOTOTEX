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
import bo.buffalo.model.AlmacenSucursal;

@ApplicationScoped
public class AlmacenSucursalRepository {
	
	@Inject
    private EntityManager em;

    public AlmacenSucursal findById(int id) {
        return em.find(AlmacenSucursal.class, id);
    }

    public List<AlmacenSucursal> findAllOrderedByFechaRegistro() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AlmacenSucursal> criteria = cb.createQuery(AlmacenSucursal.class);
        Root<AlmacenSucursal> compra = criteria.from(AlmacenSucursal.class);
        criteria.select(compra).orderBy(cb.desc(compra.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<AlmacenSucursal> findAllOrderedByID() {
    	String query = "select com from AlmacenSucursal com where com.estado='AC' or com.estado='IN' order by com.id desc";
    	System.out.println("Query AlmacenSucursals: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    
	public AlmacenSucursal findForAlmacen(Almacen alm) {
    	String query = "select com from AlmacenSucursal com where (com.estado='AC' or com.estado='IN') and com.almacen.id="+alm.getId()+" order by com.id desc";
    	System.out.println("Query AlmacenSucursals: "+query);
    	List<AlmacenSucursal> lis =em.createQuery(query).getResultList();
    	if (lis.size()>0) {
			return lis.get(0);
		}else{
			return new AlmacenSucursal();
		}
    	
    }


    
    @SuppressWarnings("unchecked")
	public List<AlmacenSucursal> traerAlmacenSucursals() {
        try {
        	String query = "select com from AlmacenSucursal com where com.estado='AC' and com.id in(select det from AlmacenSucursalProducto det where det.receta.id=com.id) order by com.id desc";
        	System.out.println("Consulta traerAlmacenSucursals: "+query);
            List<AlmacenSucursal> listaAlmacenSucursal = em.createQuery(query).getResultList();
            System.out.println("Resultado: "+listaAlmacenSucursal.size());
            return listaAlmacenSucursal;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerAlmacenSucursals: "+e.getMessage());
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
	public List<AlmacenSucursal> traerUltimas10AlmacenSucursals() {
        try {
        	String query = "select com from AlmacenSucursal com where com.estado='AC' order by com.id desc";
        	System.out.println("Consulta traerServiciosUltimas10AlmacenSucursals: "+query);
            List<AlmacenSucursal> listaAlmacenSucursal = em.createQuery(query).setMaxResults(10).getResultList();
            System.out.println("Resultado: "+listaAlmacenSucursal.size());
            return listaAlmacenSucursal;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerServiciosUltimas10AlmacenSucursals: "+e.getMessage());
			return null;
		}
    }

    
    @SuppressWarnings("unchecked")
	public List<AlmacenSucursal> findAll100UltimosAlmacenSucursals() {
        try {
        	String query = "select com from AlmacenSucursal com where com.estado='AC' order by com.id desc";
        	System.out.println("Consulta findAll100UltimosAlmacenSucursals: "+query);
            List<AlmacenSucursal> listaAlmacenSucursal = em.createQuery(query).setMaxResults(100).getResultList();
            System.out.println("Resultado: "+listaAlmacenSucursal.size());
            return listaAlmacenSucursal;        	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosAlmacenSucursals: "+e.getMessage());
			return null;
		}
    }
    
    
    public List<AlmacenSucursal> traerAlmacenSucursalsPeriodoFiscal(String gestion, String mes){
    	try {
    		System.out.println("Ingreso a traerAlmacenSucursalsPeriodoFiscal");
			String query = "select comp from AlmacenSucursal comp where comp.gestion='"+gestion+"' and comp.mes='"+mes+"'"
					+ " and comp.estado='AC' order by comp.fechaFactura asc"; 
			return em.createQuery(query).getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerAlmacenSucursalsPeriodoFiscal: "+e.getMessage());
			return null;
		}
    }
    
	
}
