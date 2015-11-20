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

import bo.buffalo.model.DetalleServicioVenta;
import bo.buffalo.model.ProformaVenta;


@ApplicationScoped
public class DetalleServicioVentaRepository {
	
	@Inject
    private EntityManager em;

    public DetalleServicioVenta findById(int id) {
        return em.find(DetalleServicioVenta.class, id);
    }

    public List<DetalleServicioVenta> findAllOrderedByProforma() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleServicioVenta> criteria = cb.createQuery(DetalleServicioVenta.class);
        Root<DetalleServicioVenta> detalle = criteria.from(DetalleServicioVenta.class);
        criteria.select(detalle).orderBy(cb.desc(detalle.get("proforma.id")));
        return em.createQuery(criteria).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalleServicioVenta> buscarPreparadosPorProforma(ProformaVenta proforma) {
    	String query = "select pro from DetalleServicioVenta pro where  pro.proforma.id="+proforma.getId()+" and pro.estado='AC'   order by pro.correlativo asc";
    	System.out.println("Query Servicios: "+query);
    	
    	List<DetalleServicioVenta> listDetalleFarmacoVenta= em.createQuery(query).getResultList();    	
    	return listDetalleFarmacoVenta;
    }
   
    @SuppressWarnings("unchecked")
	public List<DetalleServicioVenta> findAllOrderedByIDForProforma(ProformaVenta proforma) {
    	String query = "select pro from DetalleServicioVenta pro where (pro.estado='AC' or pro.estado='IN')  and pro.proforma.id="+proforma.getId()+" and (pro.proforma.estado='AC' or pro.proforma.estado='PR') order by pro.id desc";
    	System.out.println("Query Detalle Farmaco: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<DetalleServicioVenta> buscarFarmacosPorProforma(ProformaVenta proforma) {
    	String query = "select pro from DetalleServicioVenta pro where  pro.proforma.id="+proforma.getId()+" and pro.estado='AC'  order by pro.fechaRegistro desc";
    	System.out.println("Query Detalle Farmaco: "+query);
    	return em.createQuery(query).getResultList();
    }
    
    public List<DetalleServicioVenta> findAllOrderedByFechaRegistro() {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DetalleServicioVenta> criteria = cb.createQuery(DetalleServicioVenta.class);
        Root<DetalleServicioVenta> detalle = criteria.from(DetalleServicioVenta.class);
        criteria.select(detalle).orderBy(cb.desc(detalle.get("fechaRegistro")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public List<DetalleServicioVenta> findAllDetalleFarmacoVentasForDescription(String criterio) {
        try {
        	String query = "select pro from DetalleServicioVenta pro where upper(pro.servicio.nombreServicio) like '%"+criterio.toUpperCase()+"%'";
        	System.out.println("Consulta: "+query);
            return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllDetalleServicioVentaForDescription: "+e.getMessage());
			return null;
		}
    }
    
}